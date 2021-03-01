package com.shevaalex.android.rickmortydatabase.ui.episode.list

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import com.shevaalex.android.rickmortydatabase.CoroutinesTestRule
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.episode.FakeEpisodeRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EpisodeListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var viewModel: EpisodeListViewModel
    private lateinit var episodeRepository: FakeEpisodeRepository

    @Before
    fun setUp() {
        episodeRepository = FakeEpisodeRepository()
        viewModel = EpisodeListViewModel(episodeRepository)
    }

    @Test
    fun showsAllReturnsTrueWithDefaultFilter() {
        Truth.assertThat(viewModel.showsAll()).isEqualTo(true)
    }

    @Test
    fun showsAllReturnsFalseWhenAppropriateFilterSet() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(false, null)
                )
        )
        Truth.assertThat(viewModel.showsAll()).isEqualTo(false)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesWithShowsAllTrue() {
        val expectedValue = episodeRepository.suggestionNames
        val result = viewModel.suggestions.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesFilteredWithShowsAllFalse() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(false, null)
                )
        )
        val expectedValue = episodeRepository.suggestionNamesFiltered
        val result = viewModel.suggestions.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun episodeListReturnsAllEpisodesWhenQueryIsBlankAndShowsAll() {
        val allEpisodes = episodeRepository.allEpisodes
        viewModel.setNameQuery("")
        val result = viewModel.episodeList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(allEpisodes)
    }

    @Test
    fun episodeListReturnssearchAndFilterEpisodesWhenQueryIsNotBlank() {
        val filteredEpisodes = episodeRepository.filteredEpisodes
        viewModel.setNameQuery("testQuery")
        val result = viewModel.episodeList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(filteredEpisodes)
    }

    @Test
    fun episodeListReturnsSearchAndFilterEpisodesWhenShowsAllFalse() {
        val filteredEpisodes = episodeRepository.filteredEpisodes
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(false, null)
                )
        )
        val result = viewModel.episodeList.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(filteredEpisodes)
    }

    @Test
    fun saveSearchQuerySavesQueryUnaltered() = runBlockingTest {
        val query = "testQuery"
        viewModel.saveSearchQuery(query)
        Truth.assertThat(episodeRepository.query).isEqualTo(query)
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
                Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(false, null)
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
                mapOf(Constants.KEY_MAP_FILTER_EPISODE_S_ALL to Pair(false, null))
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