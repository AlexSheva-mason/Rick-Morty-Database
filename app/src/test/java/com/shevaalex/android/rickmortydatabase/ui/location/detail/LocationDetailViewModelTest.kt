package com.shevaalex.android.rickmortydatabase.ui.location.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.shevaalex.android.rickmortydatabase.LocationInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.repository.location.FakeLocationDetailRepo
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationDetailViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LocationDetailViewModel
    private lateinit var locationDetailRepo: LocationDetailRepository
    private val dataFactory = LocationInitManagerDataFactory()

            @Before
    fun setUp() {
        locationDetailRepo = FakeLocationDetailRepo()
        viewModel = LocationDetailViewModel(locationDetailRepo)
    }

    @Test
    fun viewModelReturnsCharactersAccordingToDetailObjectCharacterIds() {
        val location = dataFactory.produceObjectModel(2)
        val expectedResult = location.characterIds
        viewModel.setDetailObject(location)
        val results = viewModel.characters.getOrAwaitValueTest().map { it.id }
        Truth.assertThat(results).isEqualTo(expectedResult)
    }

    //base class  tests
    @Test
    fun callingSetMotionStateIdWithNullArgumentDoesNotUpdateValue() {
        viewModel.setMotionStateId(100)
        viewModel.setMotionStateId(null)
        Truth.assertThat(viewModel.motionStateId.value).isNotNull()
    }

    @Test
    fun setDetailObjectSavesNewValue() {
        val expectedValue = dataFactory.produceObjectModel(5)
        viewModel.setDetailObject(dataFactory.produceObjectModel(2))
        viewModel.setDetailObject(expectedValue)
        val result = viewModel.detailObject.getOrAwaitValueTest()
        Truth.assertThat(result).isEqualTo(expectedValue)
    }

}