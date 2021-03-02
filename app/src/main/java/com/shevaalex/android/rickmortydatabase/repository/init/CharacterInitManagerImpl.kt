package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.source.local.CharacterDao
import com.shevaalex.android.rickmortydatabase.source.remote.CharacterApi
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_INIT_VM_CHARACTERS_FETCHED_TIMESTAMP
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import timber.log.Timber
import javax.inject.Inject

class CharacterInitManagerImpl
@Inject
constructor(
        private val characterDao: CharacterDao,
        private val characterApi: CharacterApi,
        private val sharedPref: SharedPreferences
) : InitManager<CharacterEntity> {

    override fun getSharedPrefsKey() = KEY_INIT_VM_CHARACTERS_FETCHED_TIMESTAMP

    override suspend fun getNetworkCountApiResult(token: String): ApiResult<JsonObject> =
            characterApi.getCharacterList(idToken = token, isShallow = true)

    override suspend fun getListFromNetwork(token: String): ApiResult<List<CharacterEntity?>> {
        Timber.i("fetching characters from the rest api...")
        return characterApi.getCharacterList(idToken = token)
    }

    override suspend fun getObjectCountDb(): Int = characterDao.charactersCount()

    override suspend fun filterNetworkList(networkList: List<CharacterEntity>): List<CharacterEntity> {
        val filteredList = networkList.filter {
            val characterFromDb = characterDao.getCharacterByIdSuspend(it.id)
            it != characterFromDb
        }
        Timber.i("refetched characters filtered list size: ${filteredList.size}")
        return filteredList
    }

    override suspend fun saveNetworkListToDb(networkList: List<CharacterEntity>) {
        if (networkList.isNotEmpty()) {
            Timber.i("saving characters to DB: first character id=[%d], last character id=[%d]",
                    networkList[0].id,
                    networkList.last().id)
        } else Timber.e("saving characters to DB: character list is empty")
        characterDao.insertCharacters(networkList)
    }

    override fun getSharedPrefs(): SharedPreferences = sharedPref

}