package com.shevaalex.android.rickmortydatabase.ui.character

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.CharacterRepository
import com.shevaalex.android.rickmortydatabase.ui.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import javax.inject.Inject

class CharacterListViewModel
@Inject
constructor(
        private val characterRepository: CharacterRepository
): BaseListViewModel() {

    override val suggestions: LiveData<List<String>>
            = characterRepository.getSuggestionsNames().asLiveData()

    override val recentQueries: LiveData<List<String>>
            = characterRepository.getRecentQueries().asLiveData()

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

    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val characterList: LiveData<PagedList<CharacterModel>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if(it.first.isBlank() && showsAll()) {
                    allCharacters
                }
                // else -> perform search and/or filter the data
                else characterRepository.searchOrFilterCharacters(it.first, it.second, showsAll())
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

}