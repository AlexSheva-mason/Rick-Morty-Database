package com.shevaalex.android.rickmortydatabase

import androidx.test.core.app.ApplicationProvider

abstract class BaseTest {

    protected val application: TestRmApplication
            = ApplicationProvider.getApplicationContext() as TestRmApplication

    abstract fun injectTest()

}