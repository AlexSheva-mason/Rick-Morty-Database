package com.shevaalex.android.rickmortydatabase.di

import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.source.local.CharacterDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.EpisodeDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.LocationDaoTest
import com.shevaalex.android.rickmortydatabase.source.local.RecentQueryDaoTest
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.ReviewViewModelTest
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

    fun inject(characterDaoTest: CharacterDaoTest)

    fun inject(locationDaoTest: LocationDaoTest)

    fun inject(episodeDaoTest: EpisodeDaoTest)

    fun inject(recentQueryDaoTest: RecentQueryDaoTest)

    fun inject(reviewViewModelTest: ReviewViewModelTest)

}