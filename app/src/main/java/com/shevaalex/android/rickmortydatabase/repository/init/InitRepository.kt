package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.database.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.database.LocationModelDao
import com.shevaalex.android.rickmortydatabase.source.remote.CharacterApi
import com.shevaalex.android.rickmortydatabase.source.remote.EpisodeApi
import com.shevaalex.android.rickmortydatabase.source.remote.LocationApi
import com.shevaalex.android.rickmortydatabase.utils.currentTimeHours
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
        private val characterDao: CharacterModelDao,
        private val locationDao: LocationModelDao,
        private val episodeDao: EpisodeModelDao,
        private val characterApi: CharacterApi,
        private val locationApi: LocationApi,
        private val episodeApi: EpisodeApi
) {

    fun getDbStateResource(token: String): LiveData<StateResource> = liveData {
        emit(StateResource(Status.Loading))
        val initFuns = listOf(initCharacters(token), initLocations(token), initEpisodes(token))
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
     * Initialises a Character table in the database (does not consider the language change)
     */
    private suspend fun initCharacters(token: String): StateResource {
        val characterCountResult = getCharacterCountApiResult(token)
        if (characterCountResult is ApiResult.Success) {
            val characterCountNetwork = characterCountResult.data.size()
            val characterCountDb = getCharacterCountDb()
            if (characterCountNetwork > characterCountDb) {
                val characterNetworkListResult = fetchCharactersNetwork(token)
                return if (characterNetworkListResult is ApiResult.Success) {
                    val characterNetworkList = characterNetworkListResult.data.filterNotNull()
                    Timber.i(
                            "fetched character list from network, size: %s",
                            characterNetworkList.size
                    )
                    saveCharacterListToDb(characterNetworkList)
                    StateResource(Status.Success, Message.DbIsUpToDate)
                } else manageEmptyOrErrorResponse(characterNetworkListResult)
            } else {
                Timber.i(
                        "Characters fetch not needed, characterCountNetwork=%s, characterCountDb=%s",
                        characterCountNetwork,
                        characterCountDb
                )
                return StateResource(Status.Success, Message.DbIsUpToDate)
            }
        } else return manageEmptyOrErrorResponse(characterCountResult)
    }

    /**
     * Initialises a Location table in the database
     */
    private suspend fun initLocations(token: String): StateResource {
        val locationCountResult = getLocationCountApiResult(token)
        if (locationCountResult is ApiResult.Success) {
            val locationCountNetwork = locationCountResult.data.size()
            val locationCountDb = getLocationCountDb()
            if (locationCountNetwork > locationCountDb) {
                val locationNetworkListResult = fetchLocationsNetwork(token)
                return if (locationNetworkListResult is ApiResult.Success) {
                    val locationNetworkList = locationNetworkListResult.data.filterNotNull()
                    Timber.i(
                            "fetched location list from network, size: %s",
                            locationNetworkList.size
                    )
                    saveLocationListToDb(locationNetworkList)
                    StateResource(Status.Success, Message.DbIsUpToDate)
                } else manageEmptyOrErrorResponse(locationNetworkListResult)
            } else {
                Timber.i(
                        "Locations fetch not needed, locationCountNetwork=%s, locationCountDb=%s",
                        locationCountNetwork,
                        locationCountDb
                )
                return StateResource(Status.Success, Message.DbIsUpToDate)
            }
        } else return manageEmptyOrErrorResponse(locationCountResult)
    }


    /**
     * Initialises an Episode table in the database
     */
    private suspend fun initEpisodes(token: String): StateResource {
        val episodeCountResult = getEpisodeCountApiResult(token)
        if (episodeCountResult is ApiResult.Success) {
            val episodeCountNetwork = episodeCountResult.data.size()
            val episodeCountDb = getEpisodeCountDb()
            if (episodeCountNetwork > episodeCountDb) {
                val episodeNetworkListResult = fetchEpisodesNetwork(token)
                return if (episodeNetworkListResult is ApiResult.Success) {
                    val episodeNetworkList = episodeNetworkListResult.data.filterNotNull()
                    Timber.i(
                            "fetched episode list from network, size: %s",
                            episodeNetworkList.size
                    )
                    saveEpisodeListToDb(episodeNetworkList)
                    StateResource(Status.Success, Message.DbIsUpToDate)
                } else manageEmptyOrErrorResponse(episodeNetworkListResult)
            } else {
                Timber.i(
                        "Episodes fetch not needed, episodeCountNetwork=%s, episodeCountDb=%s",
                        episodeCountNetwork,
                        episodeCountDb
                )
                return StateResource(Status.Success, Message.DbIsUpToDate)
            }
        } else return manageEmptyOrErrorResponse(episodeCountResult)
    }

    /**
     * gets a shallow list of Characters from the api
     */
    private suspend fun getCharacterCountApiResult(token: String): ApiResult<JsonObject> {
        return characterApi.getCharacterList(idToken = token, isShallow = true)
    }

    /**
     * gets a shallow list of Locations from the api
     */
    private suspend fun getLocationCountApiResult(token: String): ApiResult<JsonObject> {
        return locationApi.getLocationList(idToken = token, isShallow = true)
    }

    /**
     * gets a shallow list of Episodes from the api
     */
    private suspend fun getEpisodeCountApiResult(token: String): ApiResult<JsonObject> {
        return episodeApi.getEpisodeList(idToken = token, isShallow = true)
    }

    private suspend fun getCharacterCountDb(): Int = characterDao.charactersCount()

    private suspend fun getLocationCountDb(): Int = locationDao.locationsCount()

    private suspend fun getEpisodeCountDb(): Int = episodeDao.episodesCount()

    /**
     * gets a list of Characters from the api
     */
    private suspend fun fetchCharactersNetwork(token: String): ApiResult<List<CharacterModel?>> {
        Timber.i("fetchCharactersNetwork: getting new data...")
        return characterApi.getCharacterList(idToken = token)
    }

    /**
     * gets a list of Locations from the api
     */
    private suspend fun fetchLocationsNetwork(token: String): ApiResult<List<LocationModel?>> {
        Timber.i("fetchLocationsNetwork: getting new data...")
        return locationApi.getLocationList(idToken = token)
    }

    /**
     * gets a list of Episodes from the api
     */
    private suspend fun fetchEpisodesNetwork(token: String): ApiResult<List<EpisodeModel?>> {
        Timber.i("fetchEpisodesNetwork: getting new data...")
        return episodeApi.getEpisodeList(idToken = token)
    }

    private suspend fun saveCharacterListToDb(characterNetworkList: List<CharacterModel>) {
        //add a timestamp to each model
        characterNetworkList.map {
            it.timeStamp = currentTimeHours().toInt()
        }
        if (characterNetworkList.isNotEmpty()) {
            Timber.i("saveCharacterListToDb: first character id=[%d], last character id=[%d]",
                    characterNetworkList[0].id,
                    characterNetworkList.last().id)
        } else Timber.e("saveCharacterListToDb: character list is empty")
        characterDao.insertCharacters(characterNetworkList)
    }

    private suspend fun saveLocationListToDb(locationNetworkList: List<LocationModel>) {
        //add a timestamp to each model
        locationNetworkList.map {
            it.timeStamp = currentTimeHours().toInt()
        }
        if (locationNetworkList.isNotEmpty()) {
            Timber.i("saveLocationListToDb: first location id=[%d], last location id=[%d]",
                    locationNetworkList[0].id,
                    locationNetworkList.last().id)
        } else Timber.e("saveLocationListToDb: location list is empty")
        locationDao.insertLocations(locationNetworkList)
    }

    private suspend fun saveEpisodeListToDb(episodeNetworkList: List<EpisodeModel>) {
        //add a timestamp to each model
        episodeNetworkList.map {
            it.timeStamp = currentTimeHours().toInt()
        }
        if (episodeNetworkList.isNotEmpty()) {
            Timber.i("saveEpisodeListToDb: first episode id=[%d], last episode id=[%d]",
                    episodeNetworkList[0].id,
                    episodeNetworkList.last().id)
        } else Timber.e("saveEpisodeListToDb: episode list is empty")
        episodeDao.insertEpisodes(episodeNetworkList)
    }

    /**
     * returns a Status.Error according to an error
     */
    private fun <T> manageEmptyOrErrorResponse(
            notSuccessfullResponse: ApiResult<T>?
    ): StateResource =
            when (notSuccessfullResponse) {
                is ApiResult.Failure -> StateResource(
                        status = Status.Error,
                        message = Message.ServerError(notSuccessfullResponse.statusCode ?: 0)
                )
                ApiResult.NetworkError -> StateResource(
                        status = Status.Error,
                        message = Message.NetworkError
                )
                ApiResult.Empty -> StateResource(
                        status = Status.Error,
                        message = Message.EmptyResponse
                )
                else -> StateResource(
                        status = Status.Error,
                        message = Message.ServerError(0)
                )
            }

}