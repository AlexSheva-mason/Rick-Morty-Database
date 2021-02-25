package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InitRepositoryImplTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    //Test subject
    private lateinit var initRepository: InitRepository

    //Collaborators
    private lateinit var characterInit: InitManager<CharacterModel>
    private lateinit var locationInit: InitManager<LocationModel>
    private lateinit var episodeInit: InitManager<EpisodeModel>

    //Utilities
    private lateinit var observerState: Observer<StateResource>

    @Before
    fun setUp() {
        characterInit = FakeInitManager(CharacterInitManagerDataFactory())
        locationInit = FakeInitManager(LocationInitManagerDataFactory())
        episodeInit = FakeInitManager(EpisodeInitManagerDataFactory())
        observerState = mock()
        initRepository = InitRepositoryImpl(characterInit, locationInit, episodeInit)
    }

    @Test
    fun getDbStateResourceShouldReturnStatusErrorIfCharacterInitManagerEmitsError() = runBlockingTest {
        (characterInit as FakeInitManager).setShouldReturnNetworkError(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedErrorState = StateResource(Status.Error, Message.ServerError(statusCode = 0))
        initRepository.getDbStateResource("any").observeForTesting(observerState) {
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, errorState) = allValues
                //expect first emitted value to be StateResource(Status.Loading)
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                //expect last emitted value to be StateResource(Status.Error)
                assertThat(errorState).isEqualTo(expectedErrorState)
            }
        }
    }

    @Test
    fun getDbStateResourceShouldReturnStatusErrorIfLocationInitManagerEmitsError() = runBlockingTest {
        (locationInit as FakeInitManager).setShouldReturnNetworkError(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedErrorState = StateResource(Status.Error, Message.ServerError(statusCode = 0))
        initRepository.getDbStateResource("any").observeForTesting(observerState){
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, errorState) = allValues
                //expect first emitted value to be StateResource(Status.Loading)
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                //expect last emitted value to be StateResource(Status.Error)
                assertThat(errorState).isEqualTo(expectedErrorState)
            }
        }
    }

    @Test
    fun getDbStateResourceShouldReturnStatusErrorIfEpisodeInitManagerEmitsError() = runBlockingTest {
        (episodeInit as FakeInitManager).setShouldReturnNetworkError(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedErrorState = StateResource(Status.Error, Message.ServerError(statusCode = 0))
        initRepository.getDbStateResource("any").observeForTesting(observerState){
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, errorState) = allValues
                //expect first emitted value to be StateResource(Status.Loading)
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                //expect last emitted value to be StateResource(Status.Error)
                assertThat(errorState).isEqualTo(expectedErrorState)
            }
        }
    }

    @Test
    fun getDbStateResourceShouldReturnStatusSuccessIfAllInitManagersEmitSuccess() = runBlockingTest {
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedSuccessState = StateResource(Status.Success, Message.DbIsUpToDate)
        initRepository.getDbStateResource("any").observeForTesting(observerState) {
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, successState) = allValues
                //expect first emitted value to be StateResource(Status.Loading)
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                //expect last emitted value to be StateResource(Status.Success)
                assertThat(successState).isEqualTo(expectedSuccessState)
            }
        }
    }

}