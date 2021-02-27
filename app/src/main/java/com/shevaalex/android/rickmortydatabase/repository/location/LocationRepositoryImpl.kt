package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.source.local.LocationModelDao
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl
@Inject
constructor(
        private val locationDao: LocationModelDao,
        private val recentQueryDao: RecentQueryDao
) : LocationRepository {

    override fun getAllLocations(): DataSource.Factory<Int, LocationModel> =
            locationDao.getAllLocations()

    override fun searchAndFilterLocations(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, LocationModel> {
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

    private fun searchLocations(query: String): DataSource.Factory<Int, LocationModel> =
            locationDao.searchLocations(query)

    private fun searchAndFilter(
            name: String?,
            filterMap: Map<String, Pair<Boolean, String?>>
    ): DataSource.Factory<Int, LocationModel> {
        val types = getTypeList(filterMap)
        val dimensions = getDimensionList(filterMap)
        Timber.i("types: %s \n dimensions: %s", types, dimensions)
        // if type == show all -> filter dimensions only
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_ALL]?.let {
            if (it.first) {
                return locationDao
                        .searchFilteredDimensionLocations(name, dimensions)
            }
        }
        // if dimensions == show all -> filter types only
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL]?.let {
            if (it.first) {
                return locationDao
                        .searchFilteredTypeLocations(name, types)
            }
        }
        return locationDao
                .searchFilteredTypeAndDimensionLocations(name, types, dimensions)
    }

    override suspend fun saveSearchQuery(query: String) {
        recentQueryDao.insertAndDeleteInTransaction(RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.LOCATION.type
        ))
    }

    override fun getSuggestionsNames(): Flow<List<String>> {
        return locationDao.getSuggestionsNames()
    }

    override fun getSuggestionsNamesFiltered(filterMap: Map<String, Pair<Boolean, String?>>): Flow<List<String>> {
        val types = getTypeList(filterMap)
        val dimensions = getDimensionList(filterMap)
        // if type == show all -> filter dimensions only
        filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_ALL]?.let {
            if (it.first) {
                return locationDao.getSuggestionsNamesDimensFiltered(dimensions)
            }
        }
        // if dimensions == show all -> filter types only
        filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL]?.let {
            if (it.first) {
                return locationDao.getSuggestionsNamesTypeFiltered(types)
            }
        }
        return locationDao.getSuggestionsNamesTypeAndDimensFiltered(types, dimensions)
    }

    override fun getRecentQueries(): Flow<List<String>> {
        return recentQueryDao.getRecentQueries(RecentQuery.Type.LOCATION.type)
    }

    private fun getTypeList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
        val typesWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_SPACE_ST]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_TYPE_MICRO]?.second,
        )
        return typesWithNulls.filterNotNull()
    }

    private fun getDimensionList(filterMap: Map<String, Pair<Boolean, String?>>): List<String> {
        val dimensWithNulls = listOf(
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_C_137]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG]?.second,
                filterMap[Constants.KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN]?.second
        )
        return dimensWithNulls.filterNotNull()
    }

}