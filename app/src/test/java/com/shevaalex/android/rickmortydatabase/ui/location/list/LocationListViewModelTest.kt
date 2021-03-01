package com.shevaalex.android.rickmortydatabase.ui.location.list

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import com.shevaalex.android.rickmortydatabase.CoroutinesTestRule
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.location.FakeLocationRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var viewModel: LocationListViewModel
    private lateinit var locationRepository: FakeLocationRepository

    @Before
    fun setUp() {
        locationRepository = FakeLocationRepository()
        viewModel = LocationListViewModel(locationRepository)
    }

    @Test
    fun showsAllReturnsTrueWithDefaultFilter() {
        Truth.assertThat(viewModel.showsAll()).isEqualTo(true)
    }

    @Test
    fun showsAllReturnsFalseWhenAppropriateFilterSet() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, null),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, null)
                )
        )
        Truth.assertThat(viewModel.showsAll()).isEqualTo(false)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesWithShowsAllTrue() {
        val expectedValue = locationRepository.suggestionNames
        val result = viewModel.suggestions.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesFilteredWithShowsAllFalse() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, null),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, null)
                )
        )
        val expectedValue = locationRepository.suggestionNamesFiltered
        val result = viewModel.suggestions.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun locationListReturnsAllLocationsWhenQueryIsBlankAndShowsAll() {
        val allLocations = locationRepository.allLocations
        viewModel.setNameQuery("")
        val result = viewModel.locationList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(allLocations)
    }

    @Test
    fun locationListReturnsSearchAndFilterLocationsWhenQueryIsNotBlank() {
        val filteredLocations = locationRepository.filteredLocations
        viewModel.setNameQuery("testQuery")
        val result = viewModel.locationList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(filteredLocations)
    }

    @Test
    fun locationListReturnsSearchAndFilterLocationsWhenShowsAllFalse() {
        val filteredLocations = locationRepository.filteredLocations
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, null),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, null)
                )
        )
        val result = viewModel.locationList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(filteredLocations)
    }

    @Test
    fun saveSearchQuerySavesQueryUnaltered() = runBlockingTest {
        val query = "testQuery"
        viewModel.saveSearchQuery(query)
        Truth.assertThat(locationRepository.query).isEqualTo(query)
    }

    //base class tests
    @Test
    fun setLayoutManagerStateSavesRvListPosition() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(parcelable)
    }

    @Test
    fun setNameQuerySavesSearchQuery() {
        val expectedResult = "testQuery"
        viewModel.setNameQuery(expectedResult)
        val result = viewModel.searchQuery
        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun setNameQueryResetsRvListPositionValue() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        viewModel.setNameQuery("testQuery")
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        Truth.assertThat(result).isNull()
    }

    @Test
    fun setFilterFlagsSavesFilterMap() {
        val expectedMap = mapOf(
                Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, null),
                Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, null)
        )
        viewModel.setFilterFlags(expectedMap)
        val result = viewModel.getFilterMap()
        Truth.assertThat(result).isEqualTo(expectedMap)
    }

    @Test
    fun setFilterFlagsResetsRvListPositionValue() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, null),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, null)
                )
        )
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        Truth.assertThat(result).isNull()
    }

    @Test
    fun addLogQueryWithAlreadyExistingQueryReturnsFalse() {
        viewModel.addLogQuery("firstQuery")
        viewModel.addLogQuery("secondQuery")
        val result = viewModel.addLogQuery("firstQuery")
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun addLogQueryWithNewQueryReturnsTrue() {
        viewModel.addLogQuery("firstQuery")
        val result = viewModel.addLogQuery("secondQuery")
        Truth.assertThat(result).isTrue()
    }

}