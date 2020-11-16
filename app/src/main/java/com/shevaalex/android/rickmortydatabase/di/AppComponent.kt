package com.shevaalex.android.rickmortydatabase.di

import android.app.Application
import com.shevaalex.android.rickmortydatabase.ui.MainActivity
import com.shevaalex.android.rickmortydatabase.ui.character.list.CharactersListFragment
import com.shevaalex.android.rickmortydatabase.ui.episode.list.EpisodesListFragment
import com.shevaalex.android.rickmortydatabase.ui.location.list.LocationsListFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    DbModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    //inject into MainActivity
    fun inject(activity: MainActivity)

    //inject into CharactersListFragment
    fun inject(fragment: CharactersListFragment)

    //inject into LocationListFragment
    fun inject(fragment: LocationsListFragment)

    //inject into EpisodesListFragment
    fun inject(fragment: EpisodesListFragment)
}