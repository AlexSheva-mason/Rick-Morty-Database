package com.shevaalex.android.rickmortydatabase.utils.networking

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Author and idea from Mitch Tabian
 * https://github.com/mitchtabian/food2fork-compose/blob/master/app/src/main/java/com/codingwithmitch/food2forkcompose/presentation/util/ConnectivityManager.kt
 */
@Singleton
class ConnectivityManager
@Inject
constructor(
        appContext: Context,
) {

  private val connectionLiveData = ConnectionLiveData(appContext)

  private val networkObserver = Observer<Boolean> { isConnected -> isNetworkAvailable.value = isConnected }

  val isNetworkAvailable = MutableLiveData<Boolean>()

  fun registerConnectionObserver(){
    connectionLiveData.observeForever(networkObserver)
  }

  fun unregisterConnectionObserver(){
    connectionLiveData.removeObserver(networkObserver)
  }

}
