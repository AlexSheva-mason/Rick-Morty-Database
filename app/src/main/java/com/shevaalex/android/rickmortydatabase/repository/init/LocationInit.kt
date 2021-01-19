package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.source.database.LocationModelDao
import com.shevaalex.android.rickmortydatabase.source.remote.LocationApi
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import javax.inject.Inject

class LocationInit
@Inject
constructor(
        private val locationDao: LocationModelDao,
        private val locationApi: LocationApi,
        private val sharedPref: SharedPreferences
) {

    private val sharedPrefsKey: String = Constants.KEY_INIT_VM_LOCATIONS_FETCHED_TIMESTAMP

    fun isLocationRefetchNeeded() = isRefetchNeeded(sharedPref, sharedPrefsKey)

    suspend fun fetchAndSaveToDbLocations(token: String): StateResource {
        val locationNetworkListResult = fetchLocationsNetwork(token)
        return if (locationNetworkListResult is ApiResult.Success) {
            val locationNetworkList = locationNetworkListResult.data.filterNotNull()
            Timber.i("fetched location list from network, size: ${locationNetworkList.size}")
            val newOrUpdatedLocations = filterLocationLists(locationNetworkList)
            saveFetchedTimestampToSharedPrefs(sharedPref, sharedPrefsKey)
            if (newOrUpdatedLocations.isNotEmpty()) {
                saveLocationListToDb(newOrUpdatedLocations)
            } else {
                Timber.i("all network/db locations are equal")
            }
            StateResource(Status.Success, Message.DbIsUpToDate)
        } else manageEmptyOrErrorResponse(locationNetworkListResult)
    }

    /**
     * gets a shallow list of Locations from the api
     */
    suspend fun getLocationCountApiResult(token: String): ApiResult<JsonObject> {
        return locationApi.getLocationList(idToken = token, isShallow = true)
    }

    suspend fun getLocationCountDb(): Int = locationDao.locationsCount()

    /**
     * filters a list of network objects with db objects
     * @returns list of network objects that differ (were updated)
     */
    private suspend fun filterLocationLists(
            locationNetworkList: List<LocationModel>
    ): List<LocationModel> {
        val filteredList = locationNetworkList.filter {
            val locationFromDb = locationDao.getLocationByIdSuspend(it.id)
            it != locationFromDb
        }
        Timber.i("refetched locations filtered list size: ${filteredList.size}")
        return filteredList
    }

    /**
     * gets a list of Locations from the api
     */
    private suspend fun fetchLocationsNetwork(token: String): ApiResult<List<LocationModel?>> {
        Timber.i("fetchLocationsNetwork: getting new data...")
        return locationApi.getLocationList(idToken = token)
    }

    private suspend fun saveLocationListToDb(locationNetworkList: List<LocationModel>) {
        if (locationNetworkList.isNotEmpty()) {
            Timber.i("saveLocationListToDb: first location id=[%d], last location id=[%d]",
                    locationNetworkList[0].id,
                    locationNetworkList.last().id)
        } else Timber.e("saveLocationListToDb: location list is empty")
        locationDao.insertLocations(locationNetworkList)
    }

}