package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepositoryImpl
@Inject
constructor(
        private val episodeDao: EpisodeModelDao,
        private val recentQueryDao: RecentQueryDao
) : EpisodeRepository {

    override fun getAllEpisodes(): DataSource.Factory<Int, EpisodeModel> =
            episodeDao.getAllEpisodes()

    override fun searchAndFilterEpisodes(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, EpisodeModel> {
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

    private fun searchEpisodes(query: String): DataSource.Factory<Int, EpisodeModel> =
            episodeDao.searchEpisodes(query)

    private fun searchAndFilter(
            name: String?,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): DataSource.Factory<Int, EpisodeModel> {
        val seasons = getSeasonsList(filterMap)
        Timber.i("seasons: %s", seasons)
        //put a placeholder if value is null -> due to Room query returning all results when
        //                                              passing a null value for IS NULL OR check
        return episodeDao.searchFilteredEpisodes(
                name = name,
                seasonCode1 = seasons.getOrElse(0) { "placeholder" },
                seasonCode2 = seasons.getOrElse(1) { "placeholder" },
                seasonCode3 = seasons.getOrElse(2) { "placeholder" },
                seasonCode4 = seasons.getOrElse(3) { "placeholder" }
        )
    }

    override suspend fun saveSearchQuery(query: String) {
        recentQueryDao.insertAndDeleteInTransaction(RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.EPISODE.type
        ))
    }

    override fun getSuggestionsNames(): Flow<List<String>> {
        return episodeDao.getSuggestionsNames()
                .combine(episodeDao.getSuggestionsCodes()) { name, code ->
                    name + code
                }
    }

    override fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>> {
        val seasons = getSeasonsList(filterMap)
        return episodeDao.getSuggestionsNamesFiltered(
                seasonCode1 = seasons.getOrElse(0) { "placeholder" },
                seasonCode2 = seasons.getOrElse(1) { "placeholder" },
                seasonCode3 = seasons.getOrElse(2) { "placeholder" },
                seasonCode4 = seasons.getOrElse(3) { "placeholder" }
        ).combine(
                episodeDao.getSuggestionsCodesFiltered(
                        seasonCode1 = seasons.getOrElse(0) { "placeholder" },
                        seasonCode2 = seasons.getOrElse(1) { "placeholder" },
                        seasonCode3 = seasons.getOrElse(2) { "placeholder" },
                        seasonCode4 = seasons.getOrElse(3) { "placeholder" }
                )) { name, code ->
            name + code
        }
    }

    override fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.EPISODE.type)
    }

    private fun getSeasonsList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
        val mapValues = listOf(
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_01]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_02]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_03]?.second,
                filterMap[Constants.KEY_MAP_FILTER_EPISODE_S_04]?.second
        )
        return mapValues.filterNotNull()
    }

}