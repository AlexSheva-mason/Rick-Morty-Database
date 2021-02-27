package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun getAllEpisodes(): LiveData<PagedList<EpisodeModel>>

    fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): LiveData<PagedList<EpisodeModel>>

    suspend fun saveSearchQuery(query: String)

    fun getSuggestionsNames(): Flow<List<String>>

    fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>>

    fun getRecentQueries(): Flow<List<String>>

}