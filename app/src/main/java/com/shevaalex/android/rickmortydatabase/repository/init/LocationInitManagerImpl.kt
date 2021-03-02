package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.source.local.LocationDao
import com.shevaalex.android.rickmortydatabase.source.remote.LocationApi
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import timber.log.Timber
import javax.inject.Inject

class LocationInitManagerImpl
@Inject
constructor(
        private val locationDao: LocationDao,
        private val locationApi: LocationApi,
        private val sharedPref: SharedPreferences
) : InitManager<LocationEntity> {

    override fun getSharedPrefsKey(): String = Constants.KEY_INIT_VM_LOCATIONS_FETCHED_TIMESTAMP

    override suspend fun getNetworkCountApiResult(token: String): ApiResult<JsonObject> =
            locationApi.getLocationList(idToken = token, isShallow = true)

    override suspend fun getListFromNetwork(token: String): ApiResult<List<LocationEntity?>> {
        Timber.i("fetching locations from the rest api...")
        return locationApi.getLocationList(idToken = token)
    }

    override suspend fun getObjectCountDb(): Int = locationDao.locationsCount()

    override suspend fun filterNetworkList(networkList: List<LocationEntity>): List<LocationEntity> {
        val filteredList = networkList.filter {
            val locationFromDb = locationDao.getLocationByIdSuspend(it.id)
            it != locationFromDb
        }
        Timber.i("refetched locations filtered list size: ${filteredList.size}")
        return filteredList
    }

    override suspend fun saveNetworkListToDb(networkList: List<LocationEntity>) {
        if (networkList.isNotEmpty()) {
            Timber.i("saving locations to DB: first location id=[%d], last location id=[%d]",
                    networkList[0].id,
                    networkList.last().id)
        } else Timber.e("saving locations to DB: location list is empty")
        locationDao.insertLocations(networkList)
    }

    override fun getSharedPrefs() = sharedPref

}