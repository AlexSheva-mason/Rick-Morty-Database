package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.auth.AuthManager
import com.shevaalex.android.rickmortydatabase.models.AuthToken
import com.shevaalex.android.rickmortydatabase.repository.init.InitRepository
import com.shevaalex.android.rickmortydatabase.utils.*
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.AUTH_TOKEN_REFRESH_TIME
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_APP_FIRST_LAUCH
import com.shevaalex.android.rickmortydatabase.utils.networking.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

class InitViewModel
@Inject
constructor(
        private val initRepository: InitRepository,
        private val sharedPref: SharedPreferences,
        private val authManager: AuthManager,
        private val connectivityManager: ConnectivityManager,
        private val application: Application,
        private val firebaseLogger: FirebaseLogger
) : ViewModel() {

    private val isNetworkAvailable: MutableLiveData<Boolean> = connectivityManager.isNetworkAvailable

    private val authIdToken: MutableLiveData<AuthToken> = MutableLiveData()

    private val mediatorLiveData = NetworkAndTokenMediatorLiveData(isNetworkAvailable, authIdToken)

    //emits dummy livedata with network error (no internet connection)
    private val noInternetError: LiveData<StateResource> = liveData {
        emit(StateResource(Status.Error, Message.NoInternet))
    }

    //emits dummy livedata with loading status
    private val loadingStatus: LiveData<StateResource> = liveData {
        emit(StateResource(Status.Loading))
    }

    init {
        connectivityManager.registerConnectionObserver()
        viewModelScope.launch {
            setNetworkStatusDisconnect()
            setAuthToken()
        }
        checkFirstLaunch()
    }

    /**
     * waits 1sec and sets isNetworkAvailable to false (starting an app in a flight mode
     * or with internet switched off doesn't trigger ConnectivityManager.NetworkCallback()
     * in ConnectionLiveData to post any value
     */
    private suspend fun setNetworkStatusDisconnect() {
        delay(1000)
        if (isNetworkAvailable.value == null) {
            isNetworkAvailable.value = false
        }
    }

    private suspend fun setAuthToken() {
        authManager.token.collect {
            Timber.v("collecting token in viewmodel, token: %s", it?.token?.takeLast(7))
            authIdToken.value = it
        }
    }

    fun init(): LiveData<StateResource> = Transformations.switchMap(mediatorLiveData) {
        val isNetworkAvailable = it.first
        val token = it.second
        when (isNetworkAvailable) {
            true -> {
                Timber.i("CONNECTED")
                token?.let { authToken ->
                    when {
                        //if authToken is default -> sharedPrefs returned null, refetch
                        authToken == authManager.defaultToken -> {
                            Timber.e("init() call, token is default")
                            refetchAuthToken()
                            loadingStatus
                        }
                        //if token has expired - refetch and emit loadingStatus
                        hasTokenExpired(authToken) -> {
                            Timber.e("init() call, token has expired")
                            refetchAuthToken()
                            loadingStatus
                        }
                        else -> {
                            initRepository.getDbStateResource(authToken.token)
                        }
                    }
                } ?: run {
                    //if token is null - emit loadingStatus and wait for authToken to be fetched
                    Timber.e("init() call, token is null")
                    loadingStatus
                }
            }
            false -> {
                Timber.e("DISCONNECTED")
                noInternetError
            }
            else -> loadingStatus
        }
    }

    /**
     * is called when observer receives db sync success status
     */
    fun notifyDbAllSuccess() {
        connectivityManager.unregisterConnectionObserver()
    }

    private fun hasTokenExpired(authToken: AuthToken): Boolean {
        val expired = currentTimeMinutes() - authToken.timestamp > AUTH_TOKEN_REFRESH_TIME
        Timber.v("hasTokenExpired: current time=[%s] token timestamp=[%s] diff=[%s]mins expired=%s",
                currentTimeMinutes(),
                authToken.timestamp,
                currentTimeMinutes() - authToken.timestamp,
                expired)
        return expired
    }

    private fun refetchAuthToken() {
        viewModelScope.launch {
            authManager.getNewToken()
        }
    }

    private fun checkFirstLaunch() {
        val isFirstLaunch = sharedPref.getBoolean(KEY_APP_FIRST_LAUCH, true)
        if (isFirstLaunch) {
            logInstallerName()
            saveFirstLaunchBool()
        }
    }

    private fun logInstallerName() {
        with(application.packageName) {
            val installerName = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                application.packageManager?.getInstallSourceInfo(this)?.installingPackageName
            } else {
                @Suppress("DEPRECATION")
                application.packageManager?.getInstallerPackageName(this)
            }
            installerName?.let {
                firebaseLogger.logFirebaseEvent(
                        eventName = "installer_name",
                        paramKey = "installer_package_name",
                        paramValue = it
                )
            }
        }
    }

    private fun saveFirstLaunchBool() {
        with(sharedPref.edit()) {
            putBoolean(KEY_APP_FIRST_LAUCH, false)
            apply()
        }
    }

}