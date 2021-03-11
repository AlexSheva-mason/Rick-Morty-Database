package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status

class FakeInitRepository : InitRepository {

    private val stateSuccess = MutableLiveData(StateResource(Status.Success))

    override fun getDbStateResource(token: String): LiveData<StateResource> {
        return stateSuccess
    }

}