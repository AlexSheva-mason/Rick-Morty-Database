package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    fun getAllCharacters(): DataSource.Factory<Int, CharacterModel>

    fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterModel>

    suspend fun saveSearchQuery(query: String)

    fun getSuggestionsNames(): Flow<List<String>>

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>>

    fun getRecentQueries(): Flow<List<String>>

}