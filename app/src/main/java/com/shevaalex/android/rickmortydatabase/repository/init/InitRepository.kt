package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitRepository
@Inject
constructor(
        private val characterInit: CharacterInit,
        private val locationInit: LocationInit,
        private val episodeInit: EpisodeInit
) {

    fun getDbStateResource(token: String): LiveData<StateResource> = liveData {
        emit(StateResource(Status.Loading))
        val initFuns = listOf(
                initCharacters(token),
                initLocations(token),
                initEpisodes(token))
        Timber.i("init funs results: %s, %s, %s",
                initFuns[0].status.toString(),
                initFuns[1].status.toString(),
                initFuns[2].status.toString())
        //if there is at least one error whilst initiating db -> emit Status.Error
        initFuns.find { errorStateResource: StateResource ->
            errorStateResource.status == Status.Error
        }?.let {
            emit(it)
        }
        //if all results are success -> emit Status.Success
        val successStatus = { successStateResource: StateResource ->
            successStateResource.status == Status.Success
        }
        if (initFuns.all(successStatus)) {
            initFuns.find(successStatus)?.let {
                emit(it)
            }
        }
    }

    /**
     * Generic function to initialise and sync database table
     */
    private fun initTable(getObjectCountNetwork: ApiResult<JsonObject>,
                          getObjectCountDB: Int,
                          saveDataToDb: StateResource,
                          isRefetchNeeded: Boolean,
                          objectIdentifier: String
    ): StateResource {
        if (getObjectCountNetwork is ApiResult.Success) {
            val objectCountNetwork = getObjectCountNetwork.data.size()
            return when {
                //fetch objects if network list size > db list size
                objectCountNetwork > getObjectCountDB -> {
                    saveDataToDb
                }
                //refetch objects if last time fetched > OBJECT_REFETCH_PERIOD (hrs)
                isRefetchNeeded -> {
                    saveDataToDb
                }
                else -> {
                    Timber.i(
                            "$objectIdentifier fetch not needed, %s, %s",
                            "$objectIdentifier network count = $objectCountNetwork",
                            "$objectIdentifier db count = $getObjectCountDB"
                    )
                    StateResource(Status.Success, Message.DbIsUpToDate)
                }
            }
        } else return manageEmptyOrErrorResponse(getObjectCountNetwork)
    }

    /**
     * Initialises and syncs Character table in the database
     */
    private suspend fun initCharacters(token: String) = initTable(
            getObjectCountNetwork = characterInit.getCharacterCountApiResult(token),
            getObjectCountDB = characterInit.getCharacterCountDb(),
            saveDataToDb = characterInit.fetchAndSaveToDbCharacters(token),
            isRefetchNeeded = characterInit.isCharacterRefetchNeeded(),
            objectIdentifier = "Character"
    )

    /**
     * Initialises and syncs Location table in the database
     */
    private suspend fun initLocations(token: String) = initTable(
            getObjectCountNetwork = locationInit.getLocationCountApiResult(token),
            getObjectCountDB = locationInit.getLocationCountDb(),
            saveDataToDb = locationInit.fetchAndSaveToDbLocations(token),
            isRefetchNeeded = locationInit.isLocationRefetchNeeded(),
            objectIdentifier = "Location"
    )

    /**
     * Initialises and syncs Episode table in the database
     */
    private suspend fun initEpisodes(token: String) = initTable(
            getObjectCountNetwork = episodeInit.getEpisodeCountApiResult(token),
            getObjectCountDB = episodeInit.getEpisodeCountDb(),
            saveDataToDb = episodeInit.fetchAndSaveToDbEpisodes(token),
            isRefetchNeeded = episodeInit.isEpisodeRefetchNeeded(),
            objectIdentifier = "Episode"
    )

}