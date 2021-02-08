package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.currentTimeHours
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber

interface InitManager<T : ApiObjectModel> {

    fun getSharedPrefsKey(): String

    /**
     * Generic function to initialise and sync database table
     */
    suspend fun initTable(token: String,
                                  objectIdentifier: String
    ): StateResource {
        val dbObjectCount = getObjectCountDb()
        val networkCountApiResult = getNetworkCountApiResult(token)
        if (networkCountApiResult is ApiResult.Success) {
            val objectCountNetwork =
                    (getNetworkCountApiResult(token) as ApiResult.Success).data.size()
            return when {
                //fetch objects if network list size > db list size
                objectCountNetwork > dbObjectCount -> {
                    fetchFromNetworkAndSaveDb(token, objectIdentifier)
                }
                //refetch objects if last time fetched > OBJECT_REFETCH_PERIOD (hrs)
                isRefetchNeeded(getSharedPrefs(), getSharedPrefsKey()) -> {
                    fetchFromNetworkAndSaveDb(token, objectIdentifier)
                }
                else -> {
                    Timber.i(
                            "$objectIdentifier fetch not needed, %s, %s",
                            "$objectIdentifier network count = $objectCountNetwork",
                            "$objectIdentifier db count = $dbObjectCount"
                    )
                    StateResource(Status.Success, Message.DbIsUpToDate)
                }
            }
        } else return manageEmptyOrErrorResponse(networkCountApiResult)
    }

    suspend fun fetchFromNetworkAndSaveDb(token: String, objectIdentifier: String): StateResource {
        val networkListResult = getListFromNetwork(token)
        return if (networkListResult is ApiResult.Success) {
            val networkList = networkListResult.data.filterNotNull()
            Timber.i("fetched $objectIdentifier list from network, size: ${networkList.size}")
            val newOrUpdatedList = filterNetworkList(networkList)
            saveFetchedTimestampToSharedPrefs(getSharedPrefs(), getSharedPrefsKey())
            if (newOrUpdatedList.isNotEmpty()) {
                saveNetworkListToDb(newOrUpdatedList)
            } else {
                Timber.i("all network/db $objectIdentifier are equal")
            }
            StateResource(Status.Success, Message.DbIsUpToDate)
        } else manageEmptyOrErrorResponse(networkListResult)
    }

    /**
     * gets a shallow list of objects from the api
     */
    suspend fun getNetworkCountApiResult(token: String): ApiResult<JsonObject>

    /**
     * gets a list of objects from the api
     */
    suspend fun getListFromNetwork(token: String): ApiResult<List<T?>>

    suspend fun getObjectCountDb(): Int

    /**
     * filters a list of network objects with db objects
     * @returns list of network objects that differ (were updated)
     */
    suspend fun filterNetworkList(networkList: List<T>): List<T>

    /**
     * saves list to database
     */
    suspend fun saveNetworkListToDb(networkList: List<T>)

    fun getSharedPrefs(): SharedPreferences

    /**
     * save the timestamp with the time when objects were refetched and saved to local db
     */
    private fun saveFetchedTimestampToSharedPrefs(sharedPref: SharedPreferences, key: String) {
        with(sharedPref.edit()) {
            val currentTimeHrs = currentTimeHours().toInt()
            Timber.d("saving $key timestamp: %s", currentTimeHrs)
            putInt(key, currentTimeHrs)
            apply()
        }
    }

    /**
     * @return true if currentTimeHrs - lastSynced is more than OBJECT_REFETCH_PERIOD (hrs)
     */
    private fun isRefetchNeeded(sharedPref: SharedPreferences, key: String): Boolean {
        val lastSynced = sharedPref.getInt(key, 0)
        val currentTimeHrs = currentTimeHours().toInt()
        Timber.d(
                "isRefetchNeeded, lastSync: %s, currentTimeHrs: %s, diff: %s, isRefetchNeeded:%s",
                lastSynced,
                currentTimeHrs,
                currentTimeHrs - lastSynced,
                currentTimeHrs - lastSynced >= Constants.OBJECT_REFETCH_PERIOD
        )
        return currentTimeHrs - lastSynced >= Constants.OBJECT_REFETCH_PERIOD
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