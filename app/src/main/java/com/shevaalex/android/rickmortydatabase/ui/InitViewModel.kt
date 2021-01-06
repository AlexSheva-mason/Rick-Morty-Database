package com.shevaalex.android.rickmortydatabase.ui

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.repository.init.InitRepository
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_ACTIVITY_MAIN_DB_SYNCED_TIMESTAMP
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
        private val initRepository: InitRepository,
        private val sharedPref: SharedPreferences
): ViewModel() {

    private val isNetworkAvailable = MutableLiveData<Boolean>()

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

    fun init(): LiveData<StateResource> = Transformations.switchMap(isNetworkAvailable) { isNetworkAvailable ->
        if (isNetworkAvailable) {
            Timber.i("CONNECTED")
            initRepository.getDbStateResource()
        } else {
            Timber.e("DISCONNECTED")
            noInternetError
        }
    }

    fun isNetworkAvailable(isConnected: Boolean) {
        if (isNetworkAvailable.value != isConnected) {
            isNetworkAvailable.value = isConnected
        }
    }

    /**
     * is called when observer receives db sync success status
     */
    fun notifyDbAllSuccess() {
        saveTimestampToSharedPrefs()
    }

    /**
     * @return true if currentTimeHrs - lastSynced is more than Const.DB_CHECK_PERIOD (hours)
     */
    fun isDbCheckNeeded(): Boolean {
        val lastSynced = sharedPref.getInt(KEY_ACTIVITY_MAIN_DB_SYNCED_TIMESTAMP, 0)
        val currentTimeHrs = (System.currentTimeMillis()/3600000).toInt()
        Timber.i(
                "getLastTimeSynced, lastSync: %s, currentTimeHrs: %s, diff: %s",
                lastSynced,
                currentTimeHrs,
                currentTimeHrs-lastSynced
        )
        return currentTimeHrs - lastSynced > Constants.DB_CHECK_PERIOD
    }

    /**
     * save the timestamp with the time when database was synced successfuly
     */
    private fun saveTimestampToSharedPrefs() {
        with (sharedPref.edit()) {
            val currentTimeHrs = (System.currentTimeMillis()/3600000).toInt()
            Timber.i("saving to share prefs timestamp: %s", currentTimeHrs)
            putInt(KEY_ACTIVITY_MAIN_DB_SYNCED_TIMESTAMP, currentTimeHrs)
            apply()
        }
    }

}