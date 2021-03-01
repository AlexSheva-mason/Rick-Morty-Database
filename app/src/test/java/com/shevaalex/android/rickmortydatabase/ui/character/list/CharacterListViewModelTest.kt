package com.shevaalex.android.rickmortydatabase.ui.character.list

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import com.shevaalex.android.rickmortydatabase.CoroutinesTestRule
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.character.FakeCharacterRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterListViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    //test subject
    private lateinit var viewModel: CharacterListViewModel

    //collaborators
    private lateinit var characterRepository: FakeCharacterRepository

    @Before
    fun setUp() {
        characterRepository = FakeCharacterRepository()
        viewModel = CharacterListViewModel(characterRepository)
    }

    @Test
    fun showsAllReturnsTrueWithDefaultFilter() {
        assertThat(viewModel.showsAll()).isEqualTo(true)
    }

    @Test
    fun showsAllReturnsFalseWhenAppropriateFilterSet() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(false, null)
                )
        )
        assertThat(viewModel.showsAll()).isEqualTo(false)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesWithShowsAllTrue() {
        val expectedValue = characterRepository.suggestionNames
        val result = viewModel.suggestions.getOrAwaitValueTest()
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun suggestionsCallsGetSuggestionsNamesFilteredWithShowsAllFalse() {
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(false, null)
                )
        )
        val expectedValue = characterRepository.suggestionNamesFiltered
        val result = viewModel.suggestions.getOrAwaitValueTest()
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun characterListReturnsAllCharactersWhenQueryIsBlankAndShowsAll() {
        val allCharacters = characterRepository.allCharacters
        viewModel.setNameQuery("")
        val result = viewModel.characterList.getOrAwaitValueTest()
        assertThat(result).isEqualTo(allCharacters)
    }

    @Test
    fun characterListReturnsSearchOrFilterCharactersWhenQueryIsNotBlank() {
        val filteredCharacters = characterRepository.filteredCharacters
        viewModel.setNameQuery("testQuery")
        val result = viewModel.characterList.getOrAwaitValueTest()
        assertThat(result).isEqualTo(filteredCharacters)
    }

    @Test
    fun characterListReturnsSearchOrFilterCharactersWhenShowsAllFalse() {
        val filteredCharacters = characterRepository.filteredCharacters
        viewModel.setFilterFlags(
                mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(false, null)
                )
        )
        val result = viewModel.characterList.getOrAwaitValueTest()
        assertThat(result).isEqualTo(filteredCharacters)
    }

    @Test
    fun saveSearchQuerySavesQueryUnaltered() = runBlockingTest {
        val query = "testQuery"
        viewModel.saveSearchQuery(query)
        assertThat(characterRepository.query).isEqualTo(query)
    }

    //base class tests
    @Test
    fun setLayoutManagerStateSavesRvListPosition() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        assertThat(result).isEqualTo(parcelable)
    }

    @Test
    fun setNameQuerySavesSearchQuery() {
        val expectedResult = "testQuery"
        viewModel.setNameQuery(expectedResult)
        val result = viewModel.searchQuery
        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun setNameQueryResetsRvListPositionValue() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        viewModel.setNameQuery("testQuery")
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        assertThat(result).isNull()
    }

    @Test
    fun setFilterFlagsSavesFilterMap() {
        val expectedMap = mapOf(
                Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(false, null),
                Constants.KEY_MAP_FILTER_STATUS_ALIVE_M to Pair(false, null),
                Constants.KEY_MAP_FILTER_STATUS_DEAD_F to Pair(false, null),
                Constants.KEY_MAP_FILTER_STATUS_DEAD_M to Pair(false, null),
                Constants.KEY_MAP_FILTER_STATUS_UNKNOWN to Pair(false, null),
                Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(false, null),
                Constants.KEY_MAP_FILTER_GENDER_MALE to Pair(false, null),
                Constants.KEY_MAP_FILTER_GENDER_GENDERLESS to Pair(false, null),
                Constants.KEY_MAP_FILTER_GENDER_UNKNOWN to Pair(false, null),
                Constants.KEY_MAP_FILTER_SPECIES_ALL to Pair(false, null),
                Constants.KEY_MAP_FILTER_SPECIES_HUMAN to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_HUMANOID to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_ALIEN to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_ANIMAL to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_ROBOT to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_POOPY to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_CRONENBERG to Pair(true, null),
                Constants.KEY_MAP_FILTER_SPECIES_MYTH to Pair(true, null),
        )
        viewModel.setFilterFlags(expectedMap)
        val result = viewModel.getFilterMap()
        assertThat(result).isEqualTo(expectedMap)
    }

    @Test
    fun setFilterFlagsResetsRvListPositionValue() {
        val parcelable = mock<Bundle>()
        viewModel.setLayoutManagerState(parcelable)
        viewModel.setFilterFlags(
                mapOf(Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(false, null))
        )
        val result = viewModel.rvListPosition.getOrAwaitValueTest()
        assertThat(result).isNull()
    }

    @Test
    fun addLogQueryWithAlreadyExistingQueryReturnsFalse() {
        viewModel.addLogQuery("firstQuery")
        viewModel.addLogQuery("secondQuery")
        val result = viewModel.addLogQuery("firstQuery")
        assertThat(result).isFalse()
    }

    @Test
    fun addLogQueryWithNewQueryReturnsTrue() {
        viewModel.addLogQuery("firstQuery")
        val result = viewModel.addLogQuery("secondQuery")
        assertThat(result).isTrue()
    }

}