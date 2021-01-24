package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.di.AppComponent
import com.shevaalex.android.rickmortydatabase.di.DaggerTestAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

class TestRmApplication: RmApplication() {

    @ExperimentalCoroutinesApi
    override val appComponent: AppComponent by lazy {
        DaggerTestAppComponent.factory().create(this)
    }

}