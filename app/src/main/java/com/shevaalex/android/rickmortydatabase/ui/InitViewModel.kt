package com.shevaalex.android.rickmortydatabase.ui

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.repository.InitRepository
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
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

    //emits dummy livedata with network error (no internet connection)
    private val noInternetError: LiveData<StateResource> = liveData {
        emit(StateResource(Status.Error, Message.NoInternet))
    }

    val test: LiveData<StateResource> = Transformations.switchMap(isNetworkAvailable) { isNetworkAvailable ->
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
        isNetworkAvailable.value = isConnected
    }

}