package com.shevaalex.android.rickmortydatabase.ui.episode.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.EpisodeInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.location.FakeLocationDetailRepo
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EpisodeDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    //test subject
    private lateinit var viewModel: EpisodeDetailViewModel

    //collaborators
    private lateinit var locationDetailRepo: LocationDetailRepository

    //utils
    private val dataFactory = EpisodeInitManagerDataFactory()

    @Before
    fun setUp() {
        locationDetailRepo = FakeLocationDetailRepo()
        viewModel = EpisodeDetailViewModel(locationDetailRepo)
    }

    @Test
    fun viewModelReturnsCharactersAccordingToDetailObjectCharacterIds() {
        val episode = dataFactory.produceObjectModel(2)
        val expectedResult = episode.characterIds
        viewModel.setDetailObject(episode)
        val results = viewModel.characters.getOrAwaitValueTest().map { it.id }
        assertThat(results).isEqualTo(expectedResult)
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