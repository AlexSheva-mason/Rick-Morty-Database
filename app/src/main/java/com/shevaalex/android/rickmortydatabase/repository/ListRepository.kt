package com.shevaalex.android.rickmortydatabase.repository

import kotlinx.coroutines.flow.Flow

interface ListRepository {

    suspend fun saveSearchQuery(query: String)

    fun getSuggestionsNames(): Flow<List<String>>

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>>

    fun getRecentQueries(): Flow<List<String>>

}