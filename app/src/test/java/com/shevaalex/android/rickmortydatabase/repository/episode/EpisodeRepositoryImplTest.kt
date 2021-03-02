package com.shevaalex.android.rickmortydatabase.repository.episode

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.assertEmittedValuesEquals
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeDao
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.testTest
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EpisodeRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //Test subject
    private lateinit var episodeRepository: EpisodeRepositoryImpl

    //Collaborators
    private lateinit var episodeDao: EpisodeDao
    private lateinit var recentQueryDao: RecentQueryDao

    @Before
    fun setUp() {
        //mock collaborators
        episodeDao = mock()
        recentQueryDao = mock()
        //instantiate subject
        episodeRepository = EpisodeRepositoryImpl(episodeDao, recentQueryDao)
    }

    /**
     * showsAll = true -> no filtering
     */
    @Test
    fun searchAndFilterEpisodesWithQueryAndShowsAllCallsSearchEpisodes() {
        val query = "testQuery"
        episodeRepository.searchAndFilterEpisodes(
                query,
                filterMap = mapOf("one" to Pair(false, "one")),
                true
        )
        //verify function call and argument
        verify(episodeDao, times(1))
                .searchEpisodes(argThat { equals(query) })
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false
     */
    @Test
    fun searchAndFilterEpisodesBlankQueryShowsAllFalseCallsSearchAndFilterEpisodesWithNullQuery() {
        val query = ""
        val placeholder = "placeholder"
        episodeRepository.searchAndFilterEpisodes(
                query,
                filterMap = mapOf("one" to Pair(false, "one")),
                false
        )
        //verify function call and arguments
        verify(episodeDao, times(1)).searchFilteredEpisodes(
                isNull(),
                eq(placeholder),
                eq(placeholder),
                eq(placeholder),
                eq(placeholder)
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false
     */
    @Test
    fun searchAndFilterEpisodesWithQueryShowsAllFalseCallsSearchAndFilterEpisodesWithQuery() {
        val query = "someQuery"
        val placeholder = "placeholder"
        episodeRepository.searchAndFilterEpisodes(
                query,
                filterMap = mapOf("one" to Pair(false, "one")),
                false
        )
        //verify function call and arguments
        verify(episodeDao, times(1)).searchFilteredEpisodes(
                query,
                placeholder,
                placeholder,
                placeholder,
                placeholder
        )
        verifyZeroInteractionsAfter()
    }

    /**
     * showsAll = false
     */
    @Test
    fun searchAndFilterEpisodesShowsAllFalseCallsSearchAndFilterEpisodesWithFilterParams() {
        val filter1 = "first"
        val filter2 = "second"
        val filter3 = "third"
        val filter4 = "fourth"
        episodeRepository.searchAndFilterEpisodes(
                "",
                filterMap = mapOf(
                        Constants.KEY_MAP_FILTER_EPISODE_S_01 to Pair(true, filter1),
                        Constants.KEY_MAP_FILTER_EPISODE_S_02 to Pair(true, filter2),
                        Constants.KEY_MAP_FILTER_EPISODE_S_03 to Pair(true, filter3),
                        Constants.KEY_MAP_FILTER_EPISODE_S_04 to Pair(true, filter4),
                ),
                false
        )
        //verify function call and arguments
        verify(episodeDao, times(1)).searchFilteredEpisodes(
                isNull(),
                eq(filter1),
                eq(filter2),
                eq(filter3),
                eq(filter4)
        )
        verifyZeroInteractionsAfter()
    }

    @Test
    fun saveSearchQueryParsesQueryCorrectly() = runBlockingTest {
        val query = "testQuery"
        val expectedValue = RecentQuery(
                id = 0,
                name = query,
                RecentQuery.Type.EPISODE.type
        )
        episodeRepository.saveSearchQuery(query)
        //verify function call and arguments
        verify(recentQueryDao, times(1)).insertAndDeleteInTransaction(expectedValue)
        verifyZeroInteractionsAfter()
    }

    @Test
    fun getSuggestionsNamesCombinesFlowsProperly() = runBlockingTest {
        val namesList = listOf("name1", "name2", "name3")
        val codesList = listOf("code1", "code2", "code3")
        val expectedResult = namesList.plus(codesList)
        whenever(episodeDao.getSuggestionsNames()).thenReturn(flow { emit(namesList) })
        whenever(episodeDao.getSuggestionsCodes()).thenReturn(flow { emit(codesList) })
        val resultFlow = episodeRepository.getSuggestionsNames()
        val collector = resultFlow.testTest(this)
        collector.assertEmittedValuesEquals(expectedResult)
        collector.cancel()
    }

    private fun verifyZeroInteractionsAfter() {
        verifyZeroInteractions(episodeDao)
        verifyZeroInteractions(recentQueryDao)
    }

}