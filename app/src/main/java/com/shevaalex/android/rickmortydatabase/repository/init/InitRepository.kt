package com.shevaalex.android.rickmortydatabase.repository.init

import androidx.lifecycle.LiveData
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource

interface InitRepository {

    fun getDbStateResource(token: String): LiveData<StateResource>

}