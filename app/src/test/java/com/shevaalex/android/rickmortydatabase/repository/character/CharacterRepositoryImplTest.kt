package com.shevaalex.android.rickmortydatabase.repository.character

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.source.local.CharacterDao
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDao
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //Test subject
    private lateinit var characterRepository: CharacterRepositoryImpl

    //Collaborators
    private lateinit var characterDao: CharacterDao
    private lateinit var recentQueryDao: RecentQueryDao


    @Before
    fun setUp() {
        //mocking CharacterDao
        characterDao = mock()
        //mocking RecentQueryDao
        recentQueryDao = mock()
        characterRepository = CharacterRepositoryImpl(characterDao, recentQueryDao)
    }

    /**
     * showsAll = true -> no filtering
     */
    @Test
    fun searchOrFilterCharactersWithSingleQueryCallsSearchCharactersWithOneParam() {
        val query = "singleQuery"
        characterRepository.searchOrFilterCharacters(
                query = query,
                filterMap = mapOf("one" to Pair(false, "one")),
                showsAll = true
        )
        //verify characterDao.searchCharacters() called once with appropriate argument
        verify(characterDao, times(1)).searchCharacters(check {
            assertThat(it).isEqualTo(query)
        })
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * showsAll = true -> no filtering
     */
    @Test
    fun searchOrFilterCharactersWithDoubleQueryCallsSearchCharactersWithTwoParams() {
        val query = "one two"
        val expectedRearrangedQuery = "two one"
        characterRepository.searchOrFilterCharacters(
                query = query,
                filterMap = mapOf("one" to Pair(false, "one")),
                showsAll = true
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).searchCharacters(
                check { assertThat(it).isEqualTo(query) },
                check { assertThat(it).isEqualTo(expectedRearrangedQuery) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * showsAll = false -> search with filtering (species filtered)
     */
    @Test
    fun searchOrFilterCharactersCallsSearchAndFilterCharacters() {
        val query = "query"
        val status = "testStatus"
        val gender = "testGender"
        val species = "testSpecies"
        characterRepository.searchOrFilterCharacters(
                query = query,
                filterMap = mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_HUMAN to Pair(true, species)
                ),
                showsAll = false
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).searchAndFilterCharacters(
                check { assertThat(it).isEqualTo(query) },
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) },
                check { assertThat(it).isEqualTo(listOf(species)) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * showsAll = false -> search with filtering (species == ALL)
     */
    @Test
    fun searchOrFilterCharactersCallsSearchAndFilterNoSpeciesCharacters() {
        val query = "query"
        val status = "testStatus"
        val gender = "testGender"
        characterRepository.searchOrFilterCharacters(
                query = query,
                filterMap = mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_ALL to Pair(true, "species")
                ),
                showsAll = false
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).searchAndFilterNoSpeciesCharacters(
                check { assertThat(it).isEqualTo(query) },
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) },
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * showsAll = false -> no search, filtering without filtering by species (species == ALL)
     */
    @Test
    fun searchOrFilterCharactersCallsGetFilteredNoSpeciesCharacters() {
        val status = "testStatus"
        val gender = "testGender"
        characterRepository.searchOrFilterCharacters(
                query = "",
                filterMap = mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_ALL to Pair(true, "species")
                ),
                showsAll = false
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).getFilteredNoSpeciesCharacters(
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * showsAll = false -> no search, filtering with species filtered
     */
    @Test
    fun searchOrFilterCharactersCallsGetFilteredCharacters() {
        val status = "testStatus"
        val gender = "testGender"
        val species = "testSpecies"
        characterRepository.searchOrFilterCharacters(
                query = "",
                filterMap = mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_HUMAN to Pair(true, species)
                ),
                showsAll = false
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).getFilteredCharacters(
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) },
                check { assertThat(it).isEqualTo(listOf(species)) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    @Test
    fun saveSearchQueryParsesQueryCorrectly() = runBlockingTest {
        characterRepository.saveSearchQuery("test query")
        val expectedArgument = RecentQuery(
                id = 0,
                name = "test query",
                RecentQuery.Type.CHARACTER.type
        )
        //verify function call and check arguments
        verify(recentQueryDao, times(1)).insertAndDeleteInTransaction(
                check { assertThat(it).isEqualTo(expectedArgument) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * filtering without filtering by species (species == ALL)
     */
    @Test
    fun getSuggestionsNamesFilteredCallsGetSuggestionsNamesFilteredWithNoSpecies() {
        val status = "testStatus"
        val gender = "testGender"
        characterRepository.getSuggestionsNamesFiltered(
                mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_ALL to Pair(true, "species")
                )
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).getSuggestionsNamesFiltered(
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    /**
     * filtering with species filtered
     */
    @Test
    fun getSuggestionsNamesFilteredCallsGetSuggestionsNamesFilteredSpeciesFiltered() {
        val status = "testStatus"
        val gender = "testGender"
        val species = "testSpecies"
        characterRepository.getSuggestionsNamesFiltered(
                mapOf(
                        Constants.KEY_MAP_FILTER_STATUS_ALIVE_F to Pair(true, status),
                        Constants.KEY_MAP_FILTER_GENDER_FEMALE to Pair(true, gender),
                        Constants.KEY_MAP_FILTER_SPECIES_HUMAN to Pair(true, species)
                )
        )
        //verify function call and check arguments
        verify(characterDao, times(1)).getSuggestionsNamesFiltered(
                check { assertThat(it).isEqualTo(listOf(status)) },
                check { assertThat(it).isEqualTo(listOf(gender)) },
                check { assertThat(it).isEqualTo(listOf(species)) }
        )
        //no calls after
        verifyZeroInteractionMocks()
    }

    private fun verifyZeroInteractionMocks() {
        verifyZeroInteractions(characterDao)
        verifyZeroInteractions(recentQueryDao)
    }

}