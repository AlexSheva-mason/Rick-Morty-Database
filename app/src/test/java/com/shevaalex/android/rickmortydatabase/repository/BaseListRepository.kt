package com.shevaalex.android.rickmortydatabase.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

abstract class BaseListRepository : ListRepository {

    var query: String? = null
    val suggestionNames = listOf("name1", "name2", "name3", "name4")
    val suggestionNamesFiltered = listOf("name filtered 1", "name filtered 2")
    private val recentQueries = listOf("recentQuery1", "recentQuery2", "recentQuery3", "recentQuery4")

    override suspend fun saveSearchQuery(query: String) {
        this.query = query
    }

    override fun getSuggestionsNames(): Flow<List<String>> = flow {
        emit(suggestionNames)
    }

    override fun getSuggestionsNamesFiltered(
            filterMap: Map<String, Pair<Boolean, String?>>
    ): Flow<List<String>> = flow {
        emit(suggestionNamesFiltered)
    }

    override fun getRecentQueries(): Flow<List<String>> = flow {
        emit(recentQueries)
    }

}