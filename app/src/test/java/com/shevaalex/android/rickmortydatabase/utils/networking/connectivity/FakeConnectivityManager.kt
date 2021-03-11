package com.shevaalex.android.rickmortydatabase.utils.networking.connectivity

import androidx.lifecycle.MutableLiveData


class FakeConnectivityManager : ConnectivityManager {

    var isObserverRegistered: Boolean = false

    override val isNetworkAvailable = MutableLiveData<Boolean>()

    fun setNetworkAvailable(boolean: Boolean) {
        isNetworkAvailable.value = boolean
    }

    override fun registerConnectionObserver() {
        isObserverRegistered = true
    }

    override fun unregisterConnectionObserver() {
        isObserverRegistered = false
    }

}