package com.shevaalex.android.rickmortydatabase.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shevaalex.android.rickmortydatabase.models.AuthToken

class NetworkAndTokenMediatorLiveData(
        sourceIsNetworkAvailable: LiveData<Boolean>,
        sourceToken: LiveData<AuthToken>
) : MediatorLiveData<Pair<Boolean, AuthToken?>>() {

    private var isNetworkAvailable: Boolean = false
    private var token: AuthToken? = null

    init {
        addSource(sourceIsNetworkAvailable) {
            it?.let { boolean ->
                isNetworkAvailable = boolean
                value = Pair(isNetworkAvailable, token)
            }
        }

        addSource(sourceToken) {
            it?.let { authToken ->
                token = authToken
                value = Pair(isNetworkAvailable, token)
            }
        }
    }

}