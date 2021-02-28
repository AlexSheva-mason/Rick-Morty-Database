package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.createMockDataSourceFactory
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeCharacterRepository : CharacterRepository {

    var query: String? = null
    private val dataFactory = CharacterInitManagerDataFactory()

    val allCharacters = dataFactory.createFixedIdObjectList(100)
    val filteredCharacters = dataFactory.createFixedIdObjectList(50)

    override fun getAllCharacters(): DataSource.Factory<Int, CharacterModel> {
        return createMockDataSourceFactory(allCharacters)
    }

    override fun searchOrFilterCharacters(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, CharacterModel> {
        return createMockDataSourceFactory(filteredCharacters)
    }

    override suspend fun saveSearchQuery(query: String) {
        this.query = query
    }

    override fun getSuggestionsNames(): Flow<List<String>> = flow {
        emit(listOf("name1", "name2", "name3", "name4"))
    }

    override fun getSuggestionsNamesFiltered(
            filterMap: Map<String, Pair<Boolean, String?>>
    ): Flow<List<String>> = flow {
        emit(listOf("name filtered 1", "name filtered 2"))
    }

    override fun getRecentQueries(): Flow<List<String>> = flow {
        emit(listOf("recentQuery1", "recentQuery2", "recentQuery3", "recentQuery4"))
    }

}