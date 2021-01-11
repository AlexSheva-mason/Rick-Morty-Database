package com.shevaalex.android.rickmortydatabase.ui.character.list

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.auth.AuthManager
import com.shevaalex.android.rickmortydatabase.models.AuthToken
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterRepository
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import com.shevaalex.android.rickmortydatabase.utils.currentTimeHours
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.ROOM_PAGE_SIZE
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel
@Inject
constructor(
        private val characterRepository: CharacterRepository,
        authManager: AuthManager
) : BaseListViewModel() {

    private val authIdToken: StateFlow<AuthToken?> = authManager.token

    override val recentQueries: LiveData<List<String>> = characterRepository.getRecentQueries().asLiveData()

    private val allCharacters = characterRepository.getAllCharacters()

    override val filterData = MutableLiveData<Map<String, Pair<Boolean, String?>>>(mapOf(
            Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, null),
            Constants.KEY_MAP_FILTER_STATUS_ALIVE_M to Pair(true, null),
            Constants.KEY_MAP_FILTER_STATUS_DEAD_F to Pair(true, null),
            Constants.KEY_MAP_FILTER_STATUS_DEAD_M to Pair(true, null),
            Constants.KEY_MAP_FILTER_STATUS_UNKNOWN to Pair(true, null),
            Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, null),
            Constants.KEY_MAP_FILTER_GENDER_MALE to Pair(true, null),
            Constants.KEY_MAP_FILTER_GENDER_GENDERLESS to Pair(true, null),
            Constants.KEY_MAP_FILTER_GENDER_UNKNOWN to Pair(true, null),
            Constants.KEY_MAP_FILTER_SPECIES_ALL to Pair(true, null),
            Constants.KEY_MAP_FILTER_SPECIES_HUMAN to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_HUMANOID to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_ALIEN to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_ANIMAL to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_ROBOT to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_POOPY to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_CRONENBERG to Pair(false, null),
            Constants.KEY_MAP_FILTER_SPECIES_MYTH to Pair(false, null),
    ))

    override val suggestions: LiveData<List<String>> =
            Transformations.switchMap(filterData) {
                if (showsAll()) {
                    characterRepository.getSuggestionsNames().asLiveData()
                } else characterRepository.getSuggestionsNamesFiltered(it).asLiveData()
            }

    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val characterList: LiveData<PagedList<CharacterModel>> =
            Transformations.switchMap(mediatorLiveData) { pair ->
                //if query is blank and filter == showAll -> show all results
                if (pair.first.isBlank() && showsAll()) {
                    allCharacters
                            .map {
                                refreshCharactersFromNetwork(it)
                            }
                            .toLiveData(ROOM_PAGE_SIZE)
                }
                // else -> perform search and/or filter the data
                else characterRepository
                        .searchOrFilterCharacters(pair.first, pair.second, showsAll())
                        .map {
                            refreshCharactersFromNetwork(it)
                        }
                        .toLiveData(ROOM_PAGE_SIZE)
            }

    override suspend fun saveSearchQuery(query: String) {
        characterRepository.saveSearchQuery(query)
    }

    override fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_ALIVE_F)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_DEAD_F)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_UNKNOWN)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_FEMALE)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_MALE)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_GENDERLESS)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_UNKNOWN)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_SPECIES_ALL)?.first!!

    private fun refreshCharactersFromNetwork(character: CharacterModel): CharacterModel {
        val currentTimeHrs = currentTimeHours().toInt()
        val lastRefreshed = character.timeStamp
        val refreshNeeded = currentTimeHrs - character.timeStamp > Constants.OBJECT_REFETCH
        Timber.v("refreshCharacterFromNetwork(): {currentTimeHrs=[%d]} {lastRefreshed=[%d]}, last refreshed [%d]hrs ago %s",
                currentTimeHrs,
                lastRefreshed,
                currentTimeHrs - lastRefreshed,
                "refresh needed:${refreshNeeded} for character ${character.id} ${character.name}")
        if (refreshNeeded) {
            viewModelScope.launch {
                authIdToken.collect {
                    it?.token?.let { token ->
                        characterRepository.updateDatedCharacter(character, token)
                    }
                }
            }
        }
        return character
    }

}