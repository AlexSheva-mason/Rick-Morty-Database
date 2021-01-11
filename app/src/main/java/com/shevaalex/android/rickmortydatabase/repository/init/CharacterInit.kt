package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.source.database.CharacterModelDao
import com.shevaalex.android.rickmortydatabase.source.remote.CharacterApi
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_INIT_VM_CHARACTERS_FETCHED_TIMESTAMP
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import javax.inject.Inject

class CharacterInit
@Inject
constructor(
        private val characterDao: CharacterModelDao,
        private val characterApi: CharacterApi,
        private val sharedPref: SharedPreferences
){

    private val sharedPrefsKey: String = KEY_INIT_VM_CHARACTERS_FETCHED_TIMESTAMP

    /**
     * Initialises and syncs a Character table in the database
     */
    suspend fun initCharacters(token: String): StateResource {
        val characterCountResult = getCharacterCountApiResult(token)
        if (characterCountResult is ApiResult.Success) {
            val characterCountNetwork = characterCountResult.data.size()
            val characterCountDb = getCharacterCountDb()
            return when {
                //fetch characters if network list size > db list size
                characterCountNetwork > characterCountDb -> {
                    fetchAndSaveToDbCharacters(token)
                }
                //refetch characters if last time fetched > OBJECT_REFETCH_PERIOD (hrs)
                isRefetchNeeded(sharedPref, sharedPrefsKey) -> {
                    fetchAndSaveToDbCharacters(token)
                }
                else -> {
                    Timber.i(
                            "Characters fetch not needed, characterCountNetwork=%s, characterCountDb=%s",
                            characterCountNetwork,
                            characterCountDb
                    )
                    StateResource(Status.Success, Message.DbIsUpToDate)
                }
            }
        } else return manageEmptyOrErrorResponse(characterCountResult)
    }

    private suspend fun fetchAndSaveToDbCharacters(token: String): StateResource {
        val characterNetworkListResult = fetchCharactersNetwork(token)
        return if (characterNetworkListResult is ApiResult.Success) {
            val characterNetworkList = characterNetworkListResult.data.filterNotNull()
            Timber.i("fetched character list from network, size: ${characterNetworkList.size}")
            val newOrUpdatedCharacters = filterCharacterLists(characterNetworkList)
            saveFetchedTimestampToSharedPrefs(sharedPref, sharedPrefsKey)
            if (newOrUpdatedCharacters.isNotEmpty()) {
                saveCharacterListToDb(newOrUpdatedCharacters)
            } else {
                Timber.i("all network/db characters are equal")
            }
            StateResource(Status.Success, Message.DbIsUpToDate)
        } else manageEmptyOrErrorResponse(characterNetworkListResult)
    }

    /**
     * filters a list of network objects with db objects
     * @returns list of network objects that differ (were updated)
     */
    private suspend fun filterCharacterLists(
            characterNetworkList: List<CharacterModel>
    ): List<CharacterModel> {
        val filteredList = characterNetworkList.filter {
            val characterFromDb = characterDao.getCharacterByIdSuspend(it.id)
            it != characterFromDb
        }
        Timber.i("refetched characters filtered list size: ${filteredList.size}")
        return filteredList
    }

    /**
     * gets a shallow list of Characters from the api
     */
    private suspend fun getCharacterCountApiResult(token: String): ApiResult<JsonObject> {
        return characterApi.getCharacterList(idToken = token, isShallow = true)
    }

    private suspend fun getCharacterCountDb(): Int = characterDao.charactersCount()

    /**
     * gets a list of Characters from the api
     */
    private suspend fun fetchCharactersNetwork(token: String): ApiResult<List<CharacterModel?>> {
        Timber.i("fetchCharactersNetwork: getting new data...")
        return characterApi.getCharacterList(idToken = token)
    }

    private suspend fun saveCharacterListToDb(characterNetworkList: List<CharacterModel>) {
        if (characterNetworkList.isNotEmpty()) {
            Timber.i("saveCharacterListToDb: first character id=[%d], last character id=[%d]",
                    characterNetworkList[0].id,
                    characterNetworkList.last().id)
        } else Timber.e("saveCharacterListToDb: character list is empty")
        characterDao.insertCharacters(characterNetworkList)
    }

}