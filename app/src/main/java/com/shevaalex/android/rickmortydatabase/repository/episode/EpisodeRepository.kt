package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun getAllEpisodes(): DataSource.Factory<Int, EpisodeModel>

    fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, EpisodeModel>

    suspend fun saveSearchQuery(query: String)

    fun getSuggestionsNames(): Flow<List<String>>

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>>

    fun getRecentQueries(): Flow<List<String>>

}