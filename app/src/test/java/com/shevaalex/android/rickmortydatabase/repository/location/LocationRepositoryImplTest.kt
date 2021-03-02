package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.source.local.LocationDao
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before

import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //Test subject
    private lateinit var locationRepository: LocationRepositoryImpl

    //Collaborators
    private lateinit var locationDao: LocationDao
    private lateinit var recentQueryDao: RecentQueryDao

    @Before
    fun setUp() {
        //mock DAOs
        locationDao = mock()
        recentQueryDao = mock()
        //instantiate subject
        locationRepository = LocationRepositoryImpl(locationDao, recentQueryDao)
    }

    /**
     * showsAll = true -> no filtering
     */
    @Test
    fun searchAndFilterLocationsWithQueryAndShowsAllCallsSearchLocations() {
        val query = "testQuery"
        locationRepository.searchAndFilterLocations(
                query,
                mapOf("one" to Pair(false, "one")),
                true
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchLocations(query)
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false
     */
    @Test
    fun searchAndFilterLocationsBlankQueryShowsAllFalseCallsSearchAndFilterWithNullQuery() {
        val query = ""
        val type = "testType"
        val dimension = "testDimension"
        locationRepository.searchAndFilterLocations(
                query,
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension)
                ),
                false
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchFilteredTypeAndDimensionLocations(
                isNull(),
                eq(listOf(type)),
                eq(listOf(dimension))
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false
     */
    @Test
    fun searchAndFilterLocationsWithQueryShowsAllFalseCallsSearchAndFilterWithQuery() {
        val query = "testQuery"
        val type = "testType"
        val dimension = "testDimension"
        locationRepository.searchAndFilterLocations(
                query,
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension)
                ),
                false
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchFilteredTypeAndDimensionLocations(
                query,
                listOf(type),
                listOf(dimension)
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false. filtered dimensions only
     */
    @Test
    fun searchAndFilterLocationsWithDimensionsFilterCallsSearchFilteredDimensionLocations() {
        val dimension = "testDimension"
        locationRepository.searchAndFilterLocations(
                "",
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension),
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(true, "true") //type == show all
                ),
                false
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchFilteredDimensionLocations(
                isNull(),
                eq(listOf(dimension))
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false. filtered types only
     */
    @Test
    fun searchAndFilterLocationsWithTypesFilterCallsSearchFilteredTypeLocations() {
        val type = "testType"
        locationRepository.searchAndFilterLocations(
                "",
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(true, "true"), //dimensions == show all
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type)
                ),
                false
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchFilteredTypeLocations(
                isNull(),
                eq(listOf(type))
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false.
     * KEY_MAP_FILTER_LOC_TYPE_ALL = false, KEY_MAP_FILTER_LOC_DIMENS_ALL = false
     */
    @Test
    fun searchAndFilterLocationsWithTypeAndDimensionShowAllFalseCallsSearchFilteredTypeAndDimensionLocations() {
        val type = "testType"
        val dimension = "testDimension"
        locationRepository.searchAndFilterLocations(
                "",
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension)
                ),
                false
        )
        //verify function call with arguments
        verify(locationDao, times(1)).searchFilteredTypeAndDimensionLocations(
                isNull(),
                eq(listOf(type)),
                eq(listOf(dimension))
        )
        verifyZeroInteractionsAfter()
    }

    @Test
    fun saveSearchQueryParsesQueryCorrectly() = runBlockingTest {
        val query = "testQuery"
        val expectedValue = RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.LOCATION.type
        )
        locationRepository.saveSearchQuery(query)
        //verify function call and arguments
        verify(recentQueryDao, times(1)).insertAndDeleteInTransaction(expectedValue)
        verifyZeroInteractionsAfter()
    }

    /**
     * both type and dimension are filtered
     * KEY_MAP_FILTER_LOC_TYPE_ALL = false, KEY_MAP_FILTER_LOC_DIMENS_ALL = false
     */
    @Test
    fun getSuggestionsNamesFilteredCallsGetSuggestionsNamesTypeAndDimensFiltered() {
        val type = "testType"
        val dimension = "testDimension"
        locationRepository.getSuggestionsNamesFiltered(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension)
                )
        )
        verify(locationDao, times(1)).getSuggestionsNamesTypeAndDimensFiltered(
                listOf(type),
                listOf(dimension)
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * type == show all -> KEY_MAP_FILTER_LOC_TYPE_ALL = true
     * dimensions are filtered -> KEY_MAP_FILTER_LOC_DIMENS_ALL = false
     */
    @Test
    fun getSuggestionsNamesFilteredCallsGetSuggestionsNamesDimensFiltered() {
        val dimension = "testDimension"
        locationRepository.getSuggestionsNamesFiltered(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(true, "true"),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_REPLACE to Pair(true, dimension)
                )
        )
        verify(locationDao, times(1)).getSuggestionsNamesDimensFiltered(
                listOf(dimension)
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * dimensions == show all -> KEY_MAP_FILTER_LOC_DIMENS_ALL = true
     * type is filtered -> KEY_MAP_FILTER_LOC_TYPE_ALL = false
     */
    @Test
    fun getSuggestionsNamesFilteredCallsGetSuggestionsNamesTypeFiltered() {
        val type = "testType"
        locationRepository.getSuggestionsNamesFiltered(
                mapOf(
                        Constants.KEY_MAP_FILTER_LOC_TYPE_ALL to Pair(false, "false"),
                        Constants.KEY_MAP_FILTER_LOC_TYPE_PLANET to Pair(true, type),
                        Constants.KEY_MAP_FILTER_LOC_DIMENS_ALL to Pair(true, "true"),
                )
        )
        verify(locationDao, times(1)).getSuggestionsNamesTypeFiltered(
                listOf(type)
        )
        verifyZeroInteractionsAfter()
    }

    private fun verifyZeroInteractionsAfter() {
        verifyZeroInteractions(locationDao)
        verifyZeroInteractions(recentQueryDao)
    }

}