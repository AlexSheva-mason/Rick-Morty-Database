package com.shevaalex.android.rickmortydatabase.utils.networking.connectivity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author and idea from Mitch Tabian
 * https://github.com/mitchtabian/food2fork-compose/blob/master/app/src/main/java/com/codingwithmitch/food2forkcompose/presentation/util/ConnectivityManager.kt
 */
@Singleton
class ConnectivityManagerImpl
@Inject
constructor(
        application: Application,
) : ConnectivityManager {

    private val connectionLiveData = ConnectionLiveData(application)

    private val networkObserver = Observer<Boolean> { isConnected -> isNetworkAvailable.value = isConnected }

    override val isNetworkAvailable = MutableLiveData<Boolean>()

    override fun registerConnectionObserver() {
        connectionLiveData.observeForever(networkObserver)
    }

    override fun unregisterConnectionObserver() {
        connectionLiveData.removeObserver(networkObserver)
    }

}
