package com.shevaalex.android.rickmortydatabase.utils.networking

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import androidx.lifecycle.LiveData

class ConnectionLiveData(private val context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = createNetworkCallback()
        cm.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onInactive() {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
        ) {
            val isInternet = networkCapabilities.hasCapability(NET_CAPABILITY_INTERNET)
            val isValidated = networkCapabilities.hasCapability(NET_CAPABILITY_VALIDATED)
            postValue(isInternet && isValidated)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }

    }

}