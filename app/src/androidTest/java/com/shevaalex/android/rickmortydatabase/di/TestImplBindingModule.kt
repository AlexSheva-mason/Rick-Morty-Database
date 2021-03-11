package com.shevaalex.android.rickmortydatabase.di

import com.shevaalex.android.rickmortydatabase.auth.AuthManager
import com.shevaalex.android.rickmortydatabase.auth.AuthManagerImpl
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepo
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepoImpl
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterRepository
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterRepositoryImpl
import com.shevaalex.android.rickmortydatabase.repository.episode.EpisodeRepository
import com.shevaalex.android.rickmortydatabase.repository.episode.EpisodeRepositoryImpl
import com.shevaalex.android.rickmortydatabase.repository.init.*
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepoImpl
import com.shevaalex.android.rickmortydatabase.repository.location.LocationDetailRepository
import com.shevaalex.android.rickmortydatabase.repository.location.LocationRepository
import com.shevaalex.android.rickmortydatabase.repository.location.LocationRepositoryImpl
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.InitPackageManager
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.InitPackageManagerImpl
import com.shevaalex.android.rickmortydatabase.utils.firebase.FakeFirebaseLogger
import com.shevaalex.android.rickmortydatabase.utils.firebase.FirebaseLogger
import com.shevaalex.android.rickmortydatabase.utils.networking.connectivity.ConnectivityManager
import com.shevaalex.android.rickmortydatabase.utils.networking.connectivity.ConnectivityManagerImpl
import dagger.Binds
import dagger.Module

@Module
interface TestImplBindingModule {

    @Binds
    fun bindCharacterInitManager(cim: CharacterInitManagerImpl): InitManager<CharacterEntity>

    @Binds
    fun bindLocationInitManager(lim: LocationInitManagerImpl): InitManager<LocationEntity>

    @Binds
    fun bindEpisodeInitManager(eim: EpisodeInitManagerImpl): InitManager<EpisodeEntity>

    @Binds
    fun bindInitRepository(iri: InitRepositoryImpl): InitRepository

    @Binds
    fun bindCharacterDetailRepo(cdr: CharacterDetailRepoImpl): CharacterDetailRepo

    @Binds
    fun bindCharacterRepository(cr: CharacterRepositoryImpl): CharacterRepository

    @Binds
    fun bindLocationDetailRepo(ldr: LocationDetailRepoImpl): LocationDetailRepository

    @Binds
    fun bindLocationRepository (lr: LocationRepositoryImpl): LocationRepository

    @Binds
    fun bindEpisodeRepository(er: EpisodeRepositoryImpl): EpisodeRepository

    @Binds
    fun bindFirebaseLogger(firebaseLogger: FakeFirebaseLogger): FirebaseLogger

    @Binds
    fun bindConnectivityManager(connectivityManagerImpl: ConnectivityManagerImpl): ConnectivityManager

    @Binds
    fun bindInitPackageManager(initPackageManagerImpl: InitPackageManagerImpl): InitPackageManager

    @Binds
    fun bindAuthManager(authManagerImpl: AuthManagerImpl): AuthManager

}