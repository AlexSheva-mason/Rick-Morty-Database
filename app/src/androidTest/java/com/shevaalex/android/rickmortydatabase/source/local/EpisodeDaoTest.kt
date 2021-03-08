package com.shevaalex.android.rickmortydatabase.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.*
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.utils.Constants
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
class EpisodeDaoTest : BaseTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var episodeDataFactory: EpisodeDataFactory

    @Inject
    lateinit var episodeDao: EpisodeDao

    init {
        injectTest()
    }

    @Test
    fun insertEpisodesAndGetAllEpisodes() = runBlockingTest {
        val testList = insertEpisodeList()
        val fetchedList = episodeDao.getAllEpisodes().toLiveData(150).getOrAwaitValue()
        if (fetchedList.size != testList.size) {
            Assert.fail("lists size should be equal: fetchedList.size = [${fetchedList.size}]," +
                    " testList.size = [${testList.size}]")
        }
        assertThat(fetchedList).containsExactlyElementsIn(testList)
    }

    @Test
    fun episodesCount() = runBlockingTest {
        val testList = insertEpisodeList()
        val fetchedCount = episodeDao.episodesCount()
        assertThat(fetchedCount).isEqualTo(testList.size)
    }

    @Test
    fun getSuggestionsNames() = runBlockingTest {
        val testList = insertEpisodeList().sortedBy { it.id }.map { it.name }
        val namesFlow = episodeDao.getSuggestionsNames()
        val testCollector = namesFlow.test(this)
        testCollector.assertEmittedValuesEquals(testList)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsNamesFiltered() = runBlockingTest {
        val testList = insertEpisodeList()
        val expectedResult = testList
                .filter {
                    it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_02) ||
                            it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_04)
                }
                .sortedBy { it.id }
                .map { it.name }
        val fetchedListFlow = episodeDao.getSuggestionsNamesFiltered(
                seasonCode1 = "null",
                seasonCode2 = Constants.VALUE_MAP_FILTER_EPISODE_S_02,
                seasonCode3 = "null",
                seasonCode4 = Constants.VALUE_MAP_FILTER_EPISODE_S_04
        )
        val testCollector = fetchedListFlow.test(this)
        testCollector.assertEmittedValuesEquals(expectedResult)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsCodes() = runBlockingTest {
        val testList = insertEpisodeList().sortedBy { it.id }.map { it.code }
        val codesFlow = episodeDao.getSuggestionsCodes()
        val testCollector = codesFlow.test(this)
        testCollector.assertEmittedValuesEquals(testList)
        testCollector.cancel()
    }

    @Test
    fun getSuggestionsCodesFiltered() = runBlockingTest {
        val testList = insertEpisodeList()
        val expectedResult = testList
                .filter {
                    it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_01) ||
                            it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_03)
                }
                .sortedBy { it.id }
                .map { it.code }
        val fetchedListFlow = episodeDao.getSuggestionsCodesFiltered(
                seasonCode1 = Constants.VALUE_MAP_FILTER_EPISODE_S_01,
                seasonCode2 = "null",
                seasonCode3 = Constants.VALUE_MAP_FILTER_EPISODE_S_03,
                seasonCode4 = "null"
        )
        val testCollector = fetchedListFlow.test(this)
        testCollector.assertEmittedValuesEquals(expectedResult)
        testCollector.cancel()
    }

    @Test
    fun searchEpisodes() = runBlockingTest {
        val testList = insertEpisodeList()
        val expectedValue = testList.random()
        val fetchedValue = episodeDao
                .searchEpisodes(expectedValue.name)
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain > 1 item
        if (fetchedValue.size > 1) {
            Assert.fail("results should contain only 1 item," +
                    " fetchedList.size = [${fetchedValue.size}]")
        }
        assertThat(fetchedValue).containsExactly(expectedValue)
    }

    @Test
    fun searchFilteredEpisodesWithName() = runBlockingTest {
        val testList = insertEpisodeList()
        val filteredList = testList
                .filter {
                    it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_01) ||
                            it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_03)
                }
        val randomName = filteredList.random().name
        val expectedResult = filteredList.filter { it.name == randomName }
        val fetchedList = episodeDao.searchFilteredEpisodes(
                name = randomName,
                seasonCode1 = Constants.VALUE_MAP_FILTER_EPISODE_S_01,
                seasonCode2 = "null",
                seasonCode3 = Constants.VALUE_MAP_FILTER_EPISODE_S_03,
                seasonCode4 = "null"
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if results contain > 1 item
        if (fetchedList.size > 1) {
            Assert.fail("results should contain only 1 item," +
                    " fetchedList.size = [${fetchedList.size}]")
        }
        assertThat(fetchedList).isEqualTo(expectedResult)
    }

    @Test
    fun searchFilteredEpisodesNoName() = runBlockingTest {
        val testList = insertEpisodeList()
        val expectedResult = testList
                .filter {
                    it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_02) ||
                            it.code.startsWith(Constants.VALUE_MAP_FILTER_EPISODE_S_04)
                }
        val fetchedList = episodeDao.searchFilteredEpisodes(
                name = null,
                seasonCode1 = "null",
                seasonCode2 = Constants.VALUE_MAP_FILTER_EPISODE_S_02,
                seasonCode3 = "null",
                seasonCode4 = Constants.VALUE_MAP_FILTER_EPISODE_S_04
        )
                .toLiveData(150)
                .getOrAwaitValue()
        //test fails if lists have different size
        if (fetchedList.size != expectedResult.size) {
            Assert.fail("result lists should be of the same size," +
                    " fetchedList.size = [${fetchedList.size}]," +
                    " expectedResult.size = [${expectedResult.size}]")
        }
        assertThat(fetchedList).containsExactlyElementsIn(expectedResult)
    }

    @Test
    fun getEpisodesByIds() = runBlockingTest {
        val testList = insertEpisodeList()
        val expectedResult = listOf(testList.random(), testList.random(), testList.random())
        val fetchedList = episodeDao
                .getEpisodesByIds(
                        listOf(expectedResult[0].id, expectedResult[1].id, expectedResult[2].id)
                )
                .getOrAwaitValue()
        //test fails if lists have different size
        if (fetchedList.size != expectedResult.size) {
            Assert.fail("result lists should be of the same size," +
                    " fetchedList.size = [${fetchedList.size}]," +
                    " expectedResult.size = [${expectedResult.size}]")
        }
        assertThat(fetchedList).containsExactlyElementsIn(expectedResult)
    }

    @Test
    fun getEpisodeByIdSuspend() = runBlockingTest {
        val testList = insertEpisodeList()
        val randomEpisode = testList.random()
        val fetchedValue = episodeDao.getEpisodeByIdSuspend(randomEpisode.id)
        assertThat(fetchedValue).isEqualTo(randomEpisode)
    }

    /**
     * inserts fixed [number] or random number of EpisodeModels
     */
    private suspend fun insertEpisodeList(
            number: Int = Random.nextInt(50, 100)
    ): List<EpisodeEntity> {
        val testList = episodeDataFactory.createFixedIdObjectList(number)
        episodeDao.insertEpisodes(testList)
        return testList
    }

    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }

}