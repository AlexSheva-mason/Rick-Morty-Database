package com.shevaalex.android.rickmortydatabase.ui.location.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.repository.location.LocationRepository
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BaseListViewModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.ROOM_PAGE_SIZE
import com.shevaalex.android.rickmortydatabase.utils.FilterMediatorLiveData
import javax.inject.Inject

class LocationListViewModel
@Inject
constructor(
        private val locationRepository: LocationRepository
) : BaseListViewModel() {

    override val recentQueries: LiveData<List<String>> =
            locationRepository.getRecentQueries().asLiveData()

    private val allLocations = locationRepository.getAllLocations()

    override val filterData = MutableLiveData<Map<String, Pair<Boolean, String?>>>(mapOf(
            Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(true, null),
            Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(true, null)
    ))

    override val suggestions: LiveData<List<String>> =
            Transformations.switchMap(filterData) {
                if (showsAll()) {
                    locationRepository.getSuggestionsNames().asLiveData()
                } else locationRepository.getSuggestionsNamesFiltered(it).asLiveData()
            }

    override val mediatorLiveData = FilterMediatorLiveData(_searchQuery, filterData)

    val locationList: LiveData<PagedList<LocationEntity>> =
            Transformations.switchMap(mediatorLiveData) {
                //if query is blank and filter == showAll -> show all results
                if (it.first.isBlank() && showsAll()) {
                    allLocations.toLiveData(ROOM_PAGE_SIZE)
                }
                // else -> perform search and/or filter the data
                else locationRepository.searchAndFilterLocations(it.first, it.second, showsAll())
                        .toLiveData(ROOM_PAGE_SIZE)
            }

    override suspend fun saveSearchQuery(query: String) {
        locationRepository.saveSearchQuery(query)
    }

    override fun showsAll(): Boolean =
            filterData.value?.get(Constants.KEY_MAP_FILTER_LOC_TYPE_ALL)?.first!! &&
                    filterData.value?.get(Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL)?.first!!
}