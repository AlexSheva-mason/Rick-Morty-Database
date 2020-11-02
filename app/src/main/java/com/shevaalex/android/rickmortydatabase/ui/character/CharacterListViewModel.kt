package com.shevaalex.android.rickmortydatabase.ui.character

import android.os.Parcelable
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.CharacterRepository
import timber.log.Timber
import javax.inject.Inject

class CharacterListViewModel
@Inject
constructor(
        private val characterRepository: CharacterRepository
): ViewModel() {

    val suggestions: LiveData<List<String>>
            = characterRepository.getSuggestionsNames().asLiveData()

    var recentQueries: LiveData<List<String>>
            = characterRepository.getRecentQueries().asLiveData()

    private val _rvListPosition = MutableLiveData<Parcelable>()

    private val allCharacters = characterRepository.getAllCharacters()

    private val loggedQueryList = MutableLiveData(mutableSetOf(" "))

    private val _searchQuery = MutableLiveData("")

    val searchQuery: String? get() = _searchQuery.value

    val rvListPosition: Parcelable? get() = _rvListPosition.value

    val characterList: LiveData<PagedList<CharacterModel>>
            = Transformations.switchMap(_searchQuery) { query ->
        //if query is null or blank show all results
        if (query.isNullOrBlank()) {
            allCharacters
        }
        // else -> perform search
        else characterRepository.searchCharacters(query)
    }

    fun setLayoutManagerState(parcelable: Parcelable?) {
        _rvListPosition.value = parcelable
    }

    /**
     * sets the name query and resets the recyclerview list position
     */
    fun setNameQuery(name: String) {
        Timber.w("setNameQuery: %s", name)
        _rvListPosition.value = null
        _searchQuery.value = name
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

}