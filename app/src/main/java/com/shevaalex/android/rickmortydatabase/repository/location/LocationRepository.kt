package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getAllLocations(): DataSource.Factory<Int, LocationModel>

    fun searchAndFilterLocations(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, LocationModel>

    suspend fun saveSearchQuery(query: String)

    fun getSuggestionsNames(): Flow<List<String>>

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>>

    fun getRecentQueries(): Flow<List<String>>

}