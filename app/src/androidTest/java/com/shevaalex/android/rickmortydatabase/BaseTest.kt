package com.shevaalex.android.rickmortydatabase

import androidx.test.core.app.ApplicationProvider
import com.shevaalex.android.rickmortydatabase.source.local.RickMortyDatabase
import org.junit.After
import javax.inject.Inject

abstract class BaseTest {

    protected val application: TestRmApplication
            = ApplicationProvider.getApplicationContext() as TestRmApplication

    @Inject
    lateinit var database: RickMortyDatabase

    @After
    fun cleanUp() {
        database.clearAllTables()
    }

    abstract fun injectTest()

}