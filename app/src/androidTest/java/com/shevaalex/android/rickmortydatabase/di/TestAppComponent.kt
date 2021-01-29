package com.shevaalex.android.rickmortydatabase.di

import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.source.local.CharacterModelDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeModelDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.LocationModelDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDaoTest
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

    fun inject(locationDaoTest: LocationModelDaoTest)

    fun inject(episodeDaoTest: EpisodeModelDaoTest)

    fun inject(recentQueryDaoTest: RecentQueryDaoTest)

}