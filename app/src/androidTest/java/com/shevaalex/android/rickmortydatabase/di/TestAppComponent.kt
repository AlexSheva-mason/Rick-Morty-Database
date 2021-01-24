package com.shevaalex.android.rickmortydatabase.di

import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.source.local.CharacterModelDaoTest
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [
    NetworkModule::class,
    TestDbModule::class,
    TestAppModule::class
]
)
interface TestAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: TestRmApplication): TestAppComponent
    }

    fun inject(characterDaoTest: CharacterModelDaoTest)

}