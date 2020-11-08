package com.shevaalex.android.rickmortydatabase.ui.character

import android.os.Parcelable
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.CharacterRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel
@Inject
constructor(
        private val characterRepository: CharacterRepository
): ViewModel() {

    val suggestions: LiveData<List<String>>
            = characterRepository.getSuggestionsNames().asLiveData()

    val recentQueries: LiveData<List<String>>
            = characterRepository.getRecentQueries().asLiveData()

    private val _rvListPosition = MutableLiveData<Parcelable>()
    val rvListPosition: LiveData<Parcelable>
        get() = _rvListPosition

    private val allCharacters = characterRepository.getAllCharacters()

    private val loggedQueryList = MutableLiveData(mutableSetOf(" "))

    private val _searchQuery = MutableLiveData("")
    val searchQuery: String? get() = _searchQuery.value

    private val filterData = MutableLiveData<Map<String, Pair<Boolean, String?>>>(mapOf(
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
    val getFilterMap: LiveData<Map<String, Pair<Boolean, String?>>> = filterData

    private val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val characterList: LiveData<PagedList<CharacterModel>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if(it.first.isBlank() && showsAll()) {
                    allCharacters
                }
                // else -> perform search and/or filter the data
                else characterRepository.searchOrFilterCharacters(it.first, it.second, showsAll())
            }

    fun setLayoutManagerState(parcelable: Parcelable?) {
        _rvListPosition.value = parcelable
    }

    /**
     * sets the name query and resets the recyclerview list position
     */
    fun setNameQuery(name: String) {
        Timber.v("setNameQuery: %s", name)
        _rvListPosition.value = null
        _searchQuery.value = name
    }

    /**
     * sets the filtering params and resets the recyclerview list position
     */
    fun setFilterFlags(filterMap: Map<String, Pair<Boolean, String?>>) {
        if (filterData.value != filterMap) {
            _rvListPosition.value = null
            filterData.value = filterMap
        } else Timber.v("filter maps are equal")
    }

    /**
     * adds a text to list of queries
     * @return `true` -> if text has been added to existing list
     *          `false` -> if text is already present in the list (or MutableLiveData list is null)
     */
    fun addLogQuery(text: String): Boolean {
       return loggedQueryList.value?.add(text)?: false
    }

    /**
     * saves search query to db table for recent suggestions
     */
    suspend fun saveSearchQuery(query: String) {
        characterRepository.saveSearchQuery(query)
    }

    private fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_ALIVE_F)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_DEAD_F)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_STATUS_UNKNOWN)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_FEMALE)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_MALE)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_GENDERLESS)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_GENDER_UNKNOWN)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_SPECIES_ALL)?.first!!

}