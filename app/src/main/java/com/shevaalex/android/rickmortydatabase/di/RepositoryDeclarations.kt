package com.shevaalex.android.rickmortydatabase.di

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepo
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterDetailRepoImpl
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterRepository
import com.shevaalex.android.rickmortydatabase.repository.character.CharacterRepositoryImpl
import com.shevaalex.android.rickmortydatabase.repository.init.*
import dagger.Binds
import dagger.Module

@Module
interface RepositoryDeclarations {

    @Binds
    fun bindCharacterInitManager(cim: CharacterInitManagerImpl): InitManager<CharacterModel>

    @Binds
    fun bindLocationInitManager(lim: LocationInitManagerImpl): InitManager<LocationModel>

    @Binds
    fun bindEpisodeInitManager(eim: EpisodeInitManagerImpl): InitManager<EpisodeModel>

    @Binds
    fun bindInitRepository(iri: InitRepositoryImpl): InitRepository

    @Binds
    fun bindCharacterDetailRepo(cdr: CharacterDetailRepoImpl): CharacterDetailRepo

    @Binds
    fun bindCharacterRepository(cr: CharacterRepositoryImpl): CharacterRepository

}