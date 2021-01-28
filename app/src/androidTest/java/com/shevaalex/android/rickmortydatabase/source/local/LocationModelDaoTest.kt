package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.*
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.random.Random

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class LocationModelDaoTest : BaseTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var locationDataFactory: LocationDataFactory

    @Inject
    lateinit var locationDao: LocationModelDao

    init {
        injectTest()
    }

    @Test
    fun insertLocations() = runBlockingTest {
        //insert a dummy list of Locations
        val testList = insertLocationList()
        //get the data from db, assert returned list == testList
        val fetchedList = locationDao.getAllLocations().toLiveData(150).getOrAwaitValue()
        if (fetchedList.size != testList.size) {
            Assert.fail("lists size should be equal: fetchedList.size = [${fetchedList.size}]," +
                    " testList.size = [${testList.size}]")
        }
        assertThat(fetchedList).containsExactlyElementsIn(testList)
    }

    @Test
    fun locationsCount() = runBlockingTest {
        //insert a dummy list of Locations
        val testList = insertLocationList()
        val returnedCount = locationDao.locationsCount()
        //assert returned values == testList.size
        assertThat(returnedCount).isEqualTo(testList.size)
    }

    @Test
    fun getSuggestionsNames() = runBlockingTest {
        //insert a dummy list of Locations. Sort and map just as if it was returned from the db
        val testList = insertLocationList().sortedBy { it.id }.map { it.name }
        //get the data from db, assert returned value == testList
        val namesFlow = locationDao.getSuggestionsNames()
        val testCollector = namesFlow.test(this)
        testCollector.assertEmittedValuesEquals(testList)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesTypeAndDimensFiltered() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        //filter it by 2 parameters to return 3 objects
        val filterType = listOf("testType10", "testType20", "testType30")
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30")
        val expectedValues = listOf( "testName10", "testName20", "testName30")
        val namesFlow = locationDao.getSuggestionsNamesTypeAndDimensFiltered(
                types = filterType,
                dimensions = filterDimension
        )
        val testCollector = namesFlow.test(this)
        //assert that returned list contains expected values
        testCollector.assertEmittedValuesEquals(expectedValues)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesTypeFiltered() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        //filter it by 1 parameter to return 4 objects
        val filterType = listOf("testType10", "testType20", "testType30", "testType40")
        val expectedValues = listOf( "testName10", "testName20", "testName30", "testName40")
        val namesFlow = locationDao.getSuggestionsNamesTypeFiltered(types = filterType)
        val testCollector = namesFlow.test(this)
        //assert that returned list contains expected values
        testCollector.assertEmittedValuesEquals(expectedValues)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesDimensFiltered() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        //filter it by 1 parameters to return 4 objects
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30", "testDimension40")
        val expectedValues = listOf( "testName10", "testName20", "testName30", "testName40")
        val namesFlow = locationDao.getSuggestionsNamesDimensFiltered(dimensions = filterDimension)
        val testCollector = namesFlow.test(this)
        //assert that returned list contains expected values
        testCollector.assertEmittedValuesEquals(expectedValues)
        testCollector.cancel()
    }

    @Test
    fun searchLocations() = runBlockingTest {
        //insert a dummy list of Locations
        val testList = insertLocationList()
        val randomCharacter = testList.random()
        val fetchedList = locationDao
                .searchLocations(randomCharacter.name)
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain > 1 item
        if (fetchedList.size > 1) {
            Assert.fail("results should contain only 1 item," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains object with a requested name
        assertThat(fetchedList).containsExactly(randomCharacter)
    }

    @Test
    fun searchFilteredTypeAndDimensionLocationsWithName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = "testName20"
        val filterType = listOf("testType10", "testType20", "testType30")
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30")
        val expectedValue = locationDataFactory.produceObjectModel(20)
        val fetchedList = locationDao.searchFilteredTypeAndDimensionLocations(
                name = nameQuery,
                types = filterType,
                dimensions = filterDimension
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 1 item
        if (fetchedList.size != 1) {
            Assert.fail("results should contain only 1 item," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactly(expectedValue)
    }

    @Test
    fun searchFilteredTypeAndDimensionLocationsNoName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = null
        val filterType = listOf("testType10", "testType20", "testType30")
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30")
        val expectedValues = listOf(
                locationDataFactory.produceObjectModel(10),
                locationDataFactory.produceObjectModel(20),
                locationDataFactory.produceObjectModel(30)
        )
        val fetchedList = locationDao.searchFilteredTypeAndDimensionLocations(
                name = nameQuery,
                types = filterType,
                dimensions = filterDimension
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 3 items
        if (fetchedList.size != 3) {
            Assert.fail("results should contain 3 items," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactlyElementsIn(expectedValues)
    }

    @Test
    fun searchFilteredTypeLocationsWithName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = "testName30"
        val filterType = listOf("testType10", "testType20", "testType30")
        val expectedValue = locationDataFactory.produceObjectModel(30)
        val fetchedList = locationDao.searchFilteredTypeLocations(
                name = nameQuery,
                types = filterType
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 1 item
        if (fetchedList.size != 1) {
            Assert.fail("results should contain 1 item," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactly(expectedValue)
    }

    @Test
    fun searchFilteredTypeLocationsNoName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = null
        val filterType = listOf("testType10", "testType20", "testType30")
        val expectedValues = listOf(
                locationDataFactory.produceObjectModel(10),
                locationDataFactory.produceObjectModel(20),
                locationDataFactory.produceObjectModel(30)
        )
        val fetchedList = locationDao.searchFilteredTypeLocations(
                name = nameQuery,
                types = filterType
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 3 items
        if (fetchedList.size != 3) {
            Assert.fail("results should contain 3 items," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactlyElementsIn(expectedValues)
    }

    @Test
    fun searchFilteredDimensionLocationsWithName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = "testName30"
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30")
        val expectedValue = locationDataFactory.produceObjectModel(30)
        val fetchedList = locationDao.searchFilteredDimensionLocations(
                name = nameQuery,
                dimensions = filterDimension
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 1 item
        if (fetchedList.size != 1) {
            Assert.fail("results should contain 1 item," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactly(expectedValue)
    }

    @Test
    fun searchFilteredDimensionLocationsNoName() = runBlockingTest {
        //insert a dummy list of Locations
        val numberOfLocations = 100
        insertLocationList(numberOfLocations, false)
        val nameQuery = null
        val filterDimension = listOf("testDimension10", "testDimension20", "testDimension30")
        val expectedValues = listOf(
                locationDataFactory.produceObjectModel(10),
                locationDataFactory.produceObjectModel(20),
                locationDataFactory.produceObjectModel(30)
        )
        val fetchedList = locationDao.searchFilteredDimensionLocations(
                name = nameQuery,
                dimensions = filterDimension
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain more or less than 3 items
        if (fetchedList.size != 3) {
            Assert.fail("results should contain 3 items," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        //assert that returned list contains expected value
        assertThat(fetchedList).containsExactlyElementsIn(expectedValues)
    }

    @Test
    fun getLocationById() = runBlockingTest {
        val testList = insertLocationList()
        val idQuery = testList.random().id
        val fetchedLocation = locationDao.getLocationById(idQuery).getOrAwaitValue()
        assertThat(fetchedLocation.id).isEqualTo(idQuery)
    }

    @Test
    fun getLocationByIdSuspend() = runBlockingTest {
        val testList = insertLocationList()
        val idQuery = testList.random().id
        val fetchedLocation = locationDao.getLocationByIdSuspend(idQuery)
        assertThat(fetchedLocation?.id).isEqualTo(idQuery)
    }

    /**
     * inserts fixed [number] or random number of LocationModels
     * [isRandom] specifies if list should contain objects with random or fixed IDs
     */
    private suspend fun insertLocationList(
            number: Int = Random.nextInt(50, 100),
            isRandom: Boolean = true
    ): List<LocationModel> {
        val testList = if (isRandom) {
            locationDataFactory.createRandomIdObjectList(number)
        } else locationDataFactory.createFixedIdObjectList(number)
        locationDao.insertLocations(testList)
        return testList
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }

}