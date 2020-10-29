package com.shevaalex.android.rickmortydatabase.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodePageModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationPageModel
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.database.EpisodeModelDao
import com.shevaalex.android.rickmortydatabase.source.database.LocationModelDao
import com.shevaalex.android.rickmortydatabase.source.network.requests.CharacterApi
import com.shevaalex.android.rickmortydatabase.source.network.requests.EpisodeApi
import com.shevaalex.android.rickmortydatabase.source.network.requests.LocationApi
import com.shevaalex.android.rickmortydatabase.utils.UiTranslateUtils
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitRepository
@Inject
constructor(
        val application: Application,
        val characterApi: CharacterApi,
        val locationApi: LocationApi,
        val episodeApi: EpisodeApi,
        val characterDao: CharacterModelDao,
        val locationDao: LocationModelDao,
        val episodeDao: EpisodeModelDao
) {

    fun getDbStateResource(): LiveData<StateResource> = liveData {
        emit(StateResource(Status.Loading))
        val initFuns = listOf(initCharacters(), initLocations(), initEpisodes())
        Timber.i("init funs results: %s, %s, %s",
                initFuns[0].status.toString(),
                initFuns[1].status.toString(),
                initFuns[2].status.toString())
        //if there is at least one error whilst initiating db -> emit Status.Error
        initFuns.find { errorStateResource: StateResource ->
            errorStateResource.status == Status.Error }?.let {
            emit(it)
        }
        //if all results are success -> emit Status.Success
        val successStatus = {
            successStateResource: StateResource ->
            successStateResource.status == Status.Success
        }
        if (initFuns.all(successStatus)) {
            initFuns.find(successStatus)?.let {
                emit(it)
            }
        }
    }

    /**
     * Initialises a Character table in the database
     */
    private suspend fun initCharacters(): StateResource {
        return object: InitManager<CharacterModel, CharacterPageModel>(
                context = application
        ){
            override suspend fun callLastApiModel(lastModelId: Int): ApiResult<CharacterModel> {
                return characterApi.getCharacter(lastModelId)
            }

            override suspend fun getLastEntryFromDb(): CharacterModel? {
                return characterDao.getLastInCharacterTable()
            }

            override suspend fun getDbEntriesCount(): Int {
                return characterDao.charactersCount()
            }

            override suspend fun callAllPages(pageCount: Int): List<ApiResult<CharacterPageModel>>
                    = coroutineScope {
                val pageIds: Iterable<Int> = 1 .. pageCount
                pageIds.map { pageNumber ->
                    async {
                        characterApi.getCharactersPage(pageNumber.toString())
                    }
                }
                        .map { it.await() }
                        .toList()
            }

            override suspend fun createCall(): ApiResult<CharacterPageModel> {
                return characterApi.getCharactersPage(1.toString())
            }

            override suspend fun saveCallResult(pageModels: List<CharacterPageModel>) {
                val characters = pageModels.flatMap { it.characterModels }.toList()
                //translate if needed and add a timestamp to each model
                characters.map {
                    if (Locale.getDefault().language.startsWith("ru")
                            || Locale.getDefault().language.startsWith("uk")) {
                        UiTranslateUtils.getTranslatedCharacter(application, it)
                    }
                    it.timeStamp = (System.currentTimeMillis() / 86400000).toInt()
                }
                if (characters.isNotEmpty()) {
                    Timber.d("saveCallResult: first character id=[%d], last character id=[%d]",
                            characters[0].id,
                            characters.last().id)
                } else {
                    Timber.e("saveCallResult: character list is empty!")
                }
                characterDao.insertCharacters(characters)
            }

        }.getDbState()
    }

    /**
     * Initialises a Location table in the database
     */
    private suspend fun initLocations(): StateResource {
        return object: InitManager<LocationModel, LocationPageModel> (
                context = application
        ) {
            override suspend fun callLastApiModel(lastModelId: Int): ApiResult<LocationModel> {
                return locationApi.getLocation(lastModelId)
            }

            override suspend fun getLastEntryFromDb(): LocationModel? {
                return locationDao.getLastInLocationTable()
            }

            override suspend fun getDbEntriesCount(): Int {
                return locationDao.locationsCount()
            }

            override suspend fun callAllPages(pageCount: Int): List<ApiResult<LocationPageModel>>
                    = coroutineScope {
                val pageIds: Iterable<Int> = 1 .. pageCount
                pageIds.map { pageNumber ->
                    async {
                        locationApi.getLocationsPage(pageNumber.toString())
                    }
                }
                        .map { it.await() }
                        .toList()
            }

            override suspend fun createCall(): ApiResult<LocationPageModel> {
                return locationApi.getLocationsPage(1.toString())
            }

            override suspend fun saveCallResult(pageModels: List<LocationPageModel>) {
                val locations = pageModels.flatMap { it.locationModels }.toList()
                //translate if needed and add a timestamp to each model
                locations.map {
                    if (Locale.getDefault().language.startsWith("ru")
                            || Locale.getDefault().language.startsWith("uk")) {
                        UiTranslateUtils.getTranslatedLocation(application, it)
                    }
                    it.timeStamp = (System.currentTimeMillis() / 86400000).toInt()
                }
                if (locations.isNotEmpty()) {
                    Timber.d("saveCallResult: first location id=[%d], last location id=[%d]",
                            locations[0].id,
                            locations.last().id)
                } else {
                    Timber.e("saveCallResult: location list is empty!")
                }
                locationDao.insertLocations(locations)
            }

        }.getDbState()
    }

    /**
     * Initialises an Episode table in the database
     */
    private suspend fun initEpisodes(): StateResource {
        return object: InitManager<EpisodeModel, EpisodePageModel>(
                context = application
        ) {
            override suspend fun callLastApiModel(lastModelId: Int): ApiResult<EpisodeModel> {
                return episodeApi.getEpisode(lastModelId)
            }

            override suspend fun getLastEntryFromDb(): EpisodeModel? {
                return episodeDao.getLastInEpisodeTable()
            }

            override suspend fun getDbEntriesCount(): Int {
                return episodeDao.episodesCount()
            }

            override suspend fun callAllPages(pageCount: Int): List<ApiResult<EpisodePageModel>> =
                    coroutineScope {
                val pageIds: Iterable<Int> = 1 .. pageCount
                pageIds.map { pageNumber ->
                    async {
                        episodeApi.getEpisodesPage(pageNumber.toString())
                    }
                }
                        .map { it.await() }
                        .toList()
            }

            override suspend fun createCall(): ApiResult<EpisodePageModel> {
                return episodeApi.getEpisodesPage(1.toString())
            }

            override suspend fun saveCallResult(pageModels: List<EpisodePageModel>) {
                val episodes = pageModels.flatMap { it.episodeModels }.toList()
                //translate if needed and add a timestamp to each model
                episodes.map {
                    if (Locale.getDefault().language.startsWith("ru")
                            || Locale.getDefault().language.startsWith("uk")) {
                        UiTranslateUtils.getTranslatedEpisode(application, it)
                    }
                    it.timeStamp = (System.currentTimeMillis() / 86400000).toInt()
                }
                if (episodes.isNotEmpty()) {
                    Timber.d("saveCallResult: first episode id=[%d], last episode id=[%d]",
                            episodes[0].id,
                            episodes.last().id)
                } else {
                    Timber.e("saveCallResult: episode list is empty!")
                }
                episodeDao.insertEpisodes(episodes)
            }
        }.getDbState()
    }

}