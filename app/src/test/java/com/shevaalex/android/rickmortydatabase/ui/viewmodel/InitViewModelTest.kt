package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import com.shevaalex.android.rickmortydatabase.CoroutinesTestRule
import com.shevaalex.android.rickmortydatabase.auth.FakeAuthManager
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import com.shevaalex.android.rickmortydatabase.observeForTesting
import com.shevaalex.android.rickmortydatabase.repository.init.FakeInitRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.firebase.FakeTestFirebaseLogger
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import com.shevaalex.android.rickmortydatabase.utils.networking.connectivity.FakeConnectivityManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class InitViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var viewModel: InitViewModel

    private lateinit var initRepository: FakeInitRepository
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var sharedPrefEditor: SharedPreferences.Editor
    private lateinit var authManager: FakeAuthManager
    private lateinit var connectivityManager: FakeConnectivityManager
    private lateinit var initPackageManager: FakeInitPackageManager
    private lateinit var firebaseLogger: FakeTestFirebaseLogger

    private lateinit var observerState: Observer<StateResource>

    @Before
    fun setUp() {
        initRepository = FakeInitRepository()
        mockSharedPreferences()
        authManager = FakeAuthManager()
        connectivityManager = FakeConnectivityManager()
        initPackageManager = FakeInitPackageManager()
        firebaseLogger = FakeTestFirebaseLogger()
        viewModel = InitViewModel(
                initRepository,
                sharedPrefs,
                authManager,
                connectivityManager,
                initPackageManager,
                firebaseLogger
        )
        //handle a delay within a coroutine
        coroutinesTestRule.testDispatcher.advanceUntilIdle()
        //mock the observer
        observerState = mock()
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun viewModelConstructorInitShouldRegisterConnectionObserver() = runBlockingTest {
        assertThat(connectivityManager.isObserverRegistered).isTrue()
    }

    @Test
    fun viewModelInitWithIsNetworkAvailableNullShouldReturnNoInternetError() = runBlockingTest {
        val result = viewModel.init().getOrAwaitValueTest()
        //expected NoInternet error
        val expectedValue = StateResource(Status.Error, Message.NoInternet)
        assertThat(result).isEqualTo(expectedValue)
    }

    @Test
    fun viewModelInitWithIsNetworkAvailableFalseShouldReturnNoInternetError() = runBlockingTest {
        connectivityManager.setNetworkAvailable(false)
        val result = viewModel.init().getOrAwaitValueTest()
        //expected NoInternet error
        val expectedValue = StateResource(Status.Error, Message.NoInternet)
        assertThat(result).isEqualTo(expectedValue)
    }

    /**
     * isNetworkAvailable = true
     */
    @Test
    fun viewModelInitWithDefaultTokenEmitsLoadingAndSuccess() = runBlockingTest {
        connectivityManager.setNetworkAvailable(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedSuccessState = StateResource(Status.Success)
        viewModel.init().observeForTesting(observerState) {
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, successState) = allValues
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                assertThat(successState).isEqualTo(expectedSuccessState)
            }
        }
        //token should've been refetched
        assertThat(authManager.isTokenRefetched).isTrue()
    }

    /**
     * isNetworkAvailable = true
     */
    @Test
    fun viewModelInitWithExpiredTokenEmitsLoadingAndSuccess() = runBlockingTest {
        authManager.token.value = authManager.expiredToken
        connectivityManager.setNetworkAvailable(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedSuccessState = StateResource(Status.Success)
        viewModel.init().observeForTesting(observerState) {
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, successState) = allValues
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                assertThat(successState).isEqualTo(expectedSuccessState)
            }
        }
        //token should've been refetched
        assertThat(authManager.isTokenRefetched).isTrue()
    }

    /**
     * isNetworkAvailable = true
     */
    @Test
    fun viewModelInitWithUpToDateTokenEmitsLoadingAndSuccess() = runBlockingTest {
        authManager.token.value = authManager.upTodateToken
        connectivityManager.setNetworkAvailable(true)
        val expectedLoadingState = StateResource(Status.Loading)
        val expectedSuccessState = StateResource(Status.Success)
        viewModel.init().observeForTesting(observerState) {
            argumentCaptor<StateResource>().apply {
                //should emit 2 times
                verify(observerState, times(2)).onChanged(capture())
                val (loadingState, successState) = allValues
                assertThat(loadingState).isEqualTo(expectedLoadingState)
                assertThat(successState).isEqualTo(expectedSuccessState)
            }
        }
        //token shouldn't have been refetched
        assertThat(authManager.isTokenRefetched).isFalse()
    }

    @Test
    fun viewModelNotifyDbAllSuccessShouldUnregisterConnectionObserver() = runBlockingTest {
        viewModel.notifyDbAllSuccess()
        assertThat(connectivityManager.isObserverRegistered).isFalse()
    }

    @Test
    fun ifFirstLaunchLogsInstallerName() = runBlockingTest {
        //override stubbing, sharedPrefs.getBoolean to return true
        whenever(sharedPrefs.getBoolean(any(), any())).thenReturn(true)
        //re-instantiate the viewmodel
        viewModel.viewModelScope.cancel()
        viewModel = InitViewModel(
                initRepository,
                sharedPrefs,
                authManager,
                connectivityManager,
                initPackageManager,
                firebaseLogger
        )
        //check if value has been logged
        assertThat(firebaseLogger.loggedEvent).isEqualTo(Constants.INIT_INSTALLER_NAME)
    }

    @Test
    fun ifFirstLaunchSavesFirstLaunchBoolFalseToSharedPreferences() = runBlockingTest {
        //override stubbing, sharedPrefs.getBoolean to return true
        whenever(sharedPrefs.getBoolean(any(), any())).thenReturn(true)
        //re-instantiate the viewmodel
        viewModel.viewModelScope.cancel()
        viewModel = InitViewModel(
                initRepository,
                sharedPrefs,
                authManager,
                connectivityManager,
                initPackageManager,
                firebaseLogger
        )
        //verify arguments
        argumentCaptor<String>().apply {
            verify(sharedPrefEditor, times(1)).putBoolean(capture(), any())
            assertThat(allValues.last()).isEqualTo(Constants.KEY_APP_FIRST_LAUCH)
        }
        argumentCaptor<Boolean>().apply {
            verify(sharedPrefEditor, times(1)).putBoolean(any(), capture())
            assertThat(allValues.last()).isEqualTo(false)
        }
    }

    private fun mockSharedPreferences() {
        sharedPrefs = mock()
        sharedPrefEditor = mock()
        whenever(sharedPrefs.edit()).thenReturn(sharedPrefEditor)
        whenever(sharedPrefs.getBoolean(any(), any())).thenReturn(false)
    }

}