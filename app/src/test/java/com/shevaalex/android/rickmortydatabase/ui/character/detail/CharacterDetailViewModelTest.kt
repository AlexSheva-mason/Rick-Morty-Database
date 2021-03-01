package com.shevaalex.android.rickmortydatabase.ui.character.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.CharacterInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepo
import com.shevaalex.android.rickmortydatabase.repository.character.FakeCharacterDetailRepo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class CharacterDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //test subject
    private lateinit var viewModel: CharacterDetailViewModel

    //collaborators
    private lateinit var characterDetailRepo: CharacterDetailRepo

    //utils
    private lateinit var dataFactory: CharacterInitManagerDataFactory

    @Before
    fun setUp() {
        dataFactory = CharacterInitManagerDataFactory()
        characterDetailRepo = FakeCharacterDetailRepo()
        viewModel = CharacterDetailViewModel(characterDetailRepo)
    }

    @Test
    fun viewModelReturnsEpisodesAccordingToDetailObjectEpisodeIds() {
        val testCharacter = dataFactory.produceObjectModel(Random.nextInt(0, 99))
        viewModel.setDetailObject(testCharacter)
        val results = viewModel.episodes.getOrAwaitValueTest().map { it.id }
        assertThat(results).containsExactlyElementsIn(testCharacter.episodeIds)
    }

    @Test
    fun viewModelReturnsLastLocationAccordingToDetailObjectLastLocationId() {
        val testCharacter = dataFactory.produceObjectModel(Random.nextInt(0, 99))
        viewModel.setDetailObject(testCharacter)
        val result = viewModel.lastLocation.getOrAwaitValueTest()
        assertThat(result.id).isEqualTo(testCharacter.lastLocation.id)
    }

    @Test
    fun viewModelReturnsOriginLocationAccordingToDetailObjectOriginLocationId() {
        val testCharacter = dataFactory.produceObjectModel(Random.nextInt(0, 99))
        viewModel.setDetailObject(testCharacter)
        val result = viewModel.originLocation.getOrAwaitValueTest()
        assertThat(result.id).isEqualTo(testCharacter.originLocation.id)
    }

    //base class  tests
    @Test
    fun callingSetMotionStateIdWithNullArgumentDoesNotUpdateValue() {
        viewModel.setMotionStateId(100)
        viewModel.setMotionStateId(null)
        assertThat(viewModel.motionStateId.value).isNotNull()
    }

    @Test
    fun setDetailObjectSavesNewValue() {
        val expectedValue = dataFactory.produceObjectModel(5)
        viewModel.setDetailObject(dataFactory.produceObjectModel(2))
        viewModel.setDetailObject(expectedValue)
        val result = viewModel.detailObject.getOrAwaitValueTest()
        assertThat(result).isEqualTo(expectedValue)
    }

}