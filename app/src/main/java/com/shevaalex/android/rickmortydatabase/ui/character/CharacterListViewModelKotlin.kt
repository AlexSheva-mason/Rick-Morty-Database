package com.shevaalex.android.rickmortydatabase.ui.character

import android.os.Parcelable
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.repository.CharacterRepository
import javax.inject.Inject

class CharacterListViewModelKotlin
@Inject
constructor(
        private val characterRepository: CharacterRepository
): ViewModel() {

    private val _rvListPosition = MutableLiveData<Parcelable>()

    private val allCharacters = characterRepository.getAllCharacters()

    private val loggedQueryList = MutableLiveData(mutableSetOf(" "))

    private val _searchQuery = MutableLiveData("")

    val searchQuery: String? get() = _searchQuery.value

    val rvListPosition: LiveData<Parcelable> get() = _rvListPosition

    val characterList: LiveData<PagedList<CharacterModel>>
            = Transformations.switchMap(_searchQuery) { query ->
        if (query == null || query == "") {
            allCharacters
        } else characterRepository.searchCharacters(query)
    }

    fun setLayoutManagerState(parcelable: Parcelable?) {
        _rvListPosition.value = parcelable
    }

    fun setNameQuery(name: String) {
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

    suspend fun getSuggestionsNames(): List<String> {
        return characterRepository.getSuggestionsNames()
    }

}