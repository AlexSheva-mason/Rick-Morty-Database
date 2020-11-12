package com.shevaalex.android.rickmortydatabase.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.character.RecentQuery
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.source.database.LocationModelDao
import com.shevaalex.android.rickmortydatabase.source.database.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository
@Inject
constructor(
        private val locationDao: LocationModelDao,
        private val recentQueryDao: RecentQueryDao
){

    fun getAllLocations(): LiveData<PagedList<LocationModel>> =
            locationDao.getAllLocations().toLiveData(50)

    fun searchAndFilterLocations(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): LiveData<PagedList<LocationModel>> {
        // perform a search without filtering
        return if (query.isNotBlank() && showsAll) {
            searchLocations(query)
        }
        // perform a search with filtering or perform just filtering
        else {
            val name = if (query.isBlank()) null else query
            searchAndFilter(name, filterMap)
        }
    }

    private fun searchLocations(query: String): LiveData<PagedList<LocationModel>> =
            locationDao.searchLocations(query).toLiveData(50)

    private fun searchAndFilter(
            name: String?,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): LiveData<PagedList<LocationModel>> {
        val typesWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO]?.second,
        )
        val types = typesWithNulls.filterNotNull()
        val dimensWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN]?.second
        )
        val dimensions = dimensWithNulls.filterNotNull()
        Timber.i("types: %s \n dimensions: %s", types, dimensions)
        // if type == show all -> filter dimensions only
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_ALL]?.let {
            if (it.first) {
                return locationDao
                        .searchFilteredDimensionLocations(name, dimensions)
                        .toLiveData(50)
            }
        }
        // if dimensions == show all -> filter types only
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL]?.let {
            if (it.first) {
                return locationDao
                        .searchFilteredTypeLocations(name, types)
                        .toLiveData(50)
            }
        }
        return locationDao
                .searchFilteredTypeAndDimensionLocations(name, types, dimensions)
                .toLiveData(50)
    }

    suspend fun saveSearchQuery(query: String) {
        recentQueryDao.insertAndDeleteInTransaction(RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.LOCATION.type
        ))
    }

    fun getSuggestionsNames(): Flow<List<String>> {
        return locationDao.getSuggestionsNames()
    }

    fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.LOCATION.type)
    }
}