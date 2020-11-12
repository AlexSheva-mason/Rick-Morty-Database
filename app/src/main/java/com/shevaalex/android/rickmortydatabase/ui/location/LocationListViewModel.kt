package com.shevaalex.android.rickmortydatabase.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import androidx.paging.PagedList
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.LocationRepository
import com.shevaalex.android.rickmortydatabase.ui.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import javax.inject.Inject

class LocationListViewModel
@Inject
constructor(
        private val locationRepository: LocationRepository
): BaseListViewModel(){

    override val suggestions: LiveData<List<String>> =
            locationRepository.getSuggestionsNames().asLiveData()

    override val recentQueries: LiveData<List<String>> =
            locationRepository.getRecentQueries().asLiveData()

    private val allLocations = locationRepository.getAllLocations()

    override val filterData = MutableLiveData<Map<String, Pair<Boolean, String?>>>(mapOf(
            Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(true, null),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(true, null)
    ))
    val getFilterMap: LiveData<Map<String, Pair<Boolean, String?>>> = filterData

    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val locationList: LiveData<PagedList<LocationModel>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if(it.first.isBlank() && showsAll()) {
                    allLocations
                }
                // else -> perform search and/or filter the data
                else locationRepository.searchAndFilterLocations(it.first, it.second, showsAll())
            }

    override suspend fun saveSearchQuery(query: String) {
        locationRepository.saveSearchQuery(query)
    }

    override fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_LOC_TYPE_ALL)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL)?.first!!
}