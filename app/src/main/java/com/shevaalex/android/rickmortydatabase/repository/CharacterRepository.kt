package com.shevaalex.android.rickmortydatabase.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.CharacterQuery
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.database.CharacterRecentDao
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepository
@Inject
constructor(

        private val characterDao: CharacterModelDao,

        private val characterRecentDao: CharacterRecentDao

) {

    fun getAllCharacters(): LiveData<PagedList<CharacterModel>> =
            characterDao.getAllCharacters().toLiveData(50)

    fun searchCharacters(query: String): LiveData<PagedList<CharacterModel>> {
        //if query contains more than 1 word -> rearrange the query
        if (query.isNotBlank() && query.contains(" ")) {
            val rearrangedQuery = query.substringAfter(" ").trim()
                    .plus(" ")
                    .plus(query.substringBefore(" ").trim())
            Timber.v("Rearranged query: %s", rearrangedQuery)
            return characterDao.getCharacterList(query, rearrangedQuery).toLiveData(50)
        }
        return characterDao.getCharacterList(query).toLiveData(50)
    }

    suspend fun saveSearchQuery(query: String) {
        characterRecentDao.insertAndDeleteInTransaction(CharacterQuery(id = 0, name = query))
    }

    fun getSuggestionsNames(): Flow<List<String>> {
        return characterDao.getSuggestionsNames()
    }

    fun getRecentQueries(): Flow<List<String>> {
        return characterRecentDao.getRecentQueries()
    }

}