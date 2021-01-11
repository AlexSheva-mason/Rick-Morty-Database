package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.source.database.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.remote.EpisodeApi
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import javax.inject.Inject

class EpisodeInit
@Inject
constructor(
        private val episodeDao: EpisodeModelDao,
        private val episodeApi: EpisodeApi,
        private val sharedPref: SharedPreferences
) {

    private val sharedPrefsKey: String = Constants.KEY_INIT_VM_EPISODES_FETCHED_TIMESTAMP

    /**
     * Initialises and syncs an Episode table in the database
     */
    suspend fun initEpisodes(token: String): StateResource {
        val episodeCountResult = getEpisodeCountApiResult(token)
        if (episodeCountResult is ApiResult.Success) {
            val episodeCountNetwork = episodeCountResult.data.size()
            val episodeCountDb = getEpisodeCountDb()
            return when {
                //fetch episodes if network list size > db list size
                episodeCountNetwork > episodeCountDb -> {
                    fetchAndSaveToDbEpisodes(token)
                }
                //refetch episodes if last time fetched > OBJECT_REFETCH_PERIOD (hrs)
                isRefetchNeeded(sharedPref, sharedPrefsKey) -> {
                    fetchAndSaveToDbEpisodes(token)
                }
                else -> {
                    Timber.i(
                            "Episodes fetch not needed, episodeCountNetwork=%s, episodeCountDb=%s",
                            episodeCountNetwork,
                            episodeCountDb
                    )
                    StateResource(Status.Success, Message.DbIsUpToDate)
                }
            }
        } else return manageEmptyOrErrorResponse(episodeCountResult)
    }

    private suspend fun fetchAndSaveToDbEpisodes(token: String): StateResource {
        val episodeNetworkListResult = fetchEpisodesNetwork(token)
        return if (episodeNetworkListResult is ApiResult.Success) {
            val episodeNetworkList = episodeNetworkListResult.data.filterNotNull()
            Timber.i("fetched episode list from network, size: ${episodeNetworkList.size}")
            val newOrUpdatedEpisodes = filterEpisodeLists(episodeNetworkList)
            saveFetchedTimestampToSharedPrefs(sharedPref, sharedPrefsKey)
            if (newOrUpdatedEpisodes.isNotEmpty()) {
                saveEpisodeListToDb(newOrUpdatedEpisodes)
            } else {
                Timber.i("all network/db episodes are equal")
            }
            StateResource(Status.Success, Message.DbIsUpToDate)
        } else manageEmptyOrErrorResponse(episodeNetworkListResult)
    }

    /**
     * filters a list of network objects with db objects
     * @returns list of network objects that differ (were updated)
     */
    private suspend fun filterEpisodeLists(
            episodeNetworkList: List<EpisodeModel>
    ): List<EpisodeModel> {
        val filteredList = episodeNetworkList.filter {
            val episodeFromDb = episodeDao.getEpisodeByIdSuspend(it.id)
            it != episodeFromDb
        }
        Timber.i("refetched episodes filtered list size: ${filteredList.size}")
        return filteredList
    }

    /**
     * gets a shallow list of Episodes from the api
     */
    private suspend fun getEpisodeCountApiResult(token: String): ApiResult<JsonObject> {
        return episodeApi.getEpisodeList(idToken = token, isShallow = true)
    }

    private suspend fun getEpisodeCountDb(): Int = episodeDao.episodesCount()

    /**
     * gets a list of Episodes from the api
     */
    private suspend fun fetchEpisodesNetwork(token: String): ApiResult<List<EpisodeModel?>> {
        Timber.i("fetchEpisodesNetwork: getting new data...")
        return episodeApi.getEpisodeList(idToken = token)
    }

    private suspend fun saveEpisodeListToDb(episodeNetworkList: List<EpisodeModel>) {
        if (episodeNetworkList.isNotEmpty()) {
            Timber.i("saveEpisodeListToDb: first episode id=[%d], last episode id=[%d]",
                    episodeNetworkList[0].id,
                    episodeNetworkList.last().id)
        } else Timber.e("saveEpisodeListToDb: episode list is empty")
        episodeDao.insertEpisodes(episodeNetworkList)
    }

}