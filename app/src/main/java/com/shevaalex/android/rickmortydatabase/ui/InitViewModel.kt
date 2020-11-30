package com.shevaalex.android.rickmortydatabase.ui

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.repository.init.InitRepository
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class InitViewModel
@Inject
constructor(
        private val initRepository: InitRepository
): ViewModel() {

    private val isNetworkAvailable = MutableLiveData<Boolean>()

    private val _dbIsSynced = MutableLiveData(false)
    val dbIsSynced: LiveData<Boolean> get() = _dbIsSynced

    init {
        viewModelScope.launch {
            /*
            waits 1sec and sets isNetworkAvailable to false (starting an app in a flight mode
            or with internet switched off doesn't trigger ConnectivityManager.NetworkCallback()
            in ConnectionLiveData to post any value
            */
            delay(1000)
            if (isNetworkAvailable.value == null) {
                isNetworkAvailable.value = false
            }
        }
    }

    //emits dummy livedata with network error (no internet connection)
    private val noInternetError: LiveData<StateResource> = liveData {
        emit(StateResource(Status.Error, Message.NoInternet))
    }

    val init: LiveData<StateResource> = Transformations.switchMap(isNetworkAvailable) { isNetworkAvailable ->
        if (isNetworkAvailable) {
            Timber.i("CONNECTED")
            initRepository.getDbStateResource()
        } else {
            Timber.e("DISCONNECTED")
            noInternetError
        }
    }

    fun dbIsSynced (isSynced: Boolean) {
        _dbIsSynced.value = isSynced
    }

    fun isNetworkAvailable(isConnected: Boolean) {
        if (isNetworkAvailable.value != isConnected) {
            isNetworkAvailable.value = isConnected
        }
    }

}