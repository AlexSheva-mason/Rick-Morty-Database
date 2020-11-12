package com.shevaalex.android.rickmortydatabase.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.character.RecentQuery
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.source.database.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.database.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepository
@Inject
constructor(
        private val episodeDao: EpisodeModelDao,
        private val recentQueryDao: RecentQueryDao
){

    fun getAllEpisodes(): LiveData<PagedList<EpisodeModel>> =
            episodeDao.getAllEpisodes().toLiveData(50)

    fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): LiveData<PagedList<EpisodeModel>> {
        // perform a search without filtering
        return if (query.isNotBlank() && showsAll) {
            searchEpisodes(query)
        }
        // perform a search with filtering or perform just filtering
        else {
            val name = if (query.isBlank()) null else query
            searchAndFilter(name, filterMap)
        }
    }

    private fun searchEpisodes(query: String): LiveData<PagedList<EpisodeModel>> =
            episodeDao.searchEpisodes(query).toLiveData(50)

    private fun searchAndFilter(
            name: String?,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): LiveData<PagedList<EpisodeModel>> {
        val mapValues = listOf(
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_01]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_02]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_03]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_04]?.second
        )
        Timber.i("seasons: %s", mapValues)
        return episodeDao.searchFilteredEpisodes(
                name = name,
                seasonCode1 = filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_01]?.second,
                seasonCode2 = filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_02]?.second,
                seasonCode3 = filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_03]?.second,
                seasonCode4 = filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_04]?.second
        ).toLiveData(50)
    }

    suspend fun saveSearchQuery(query: String) {
        recentQueryDao.insertAndDeleteInTransaction(RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.EPISODE.type
        ))
    }

    fun getSuggestionsNames(): Flow<List<String>> {
        return episodeDao.getSuggestionsNames()
    }

    fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.EPISODE.type)
    }
}