package com.shevaalex.android.rickmortydatabase.di

import android.app.Application
import androidx.room.Room
import com.shevaalex.android.rickmortydatabase.source.local.*
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DbModule{

    @Singleton
    @Provides
    fun provideAppDb(app: Application): RickMortyDatabase {
        return Room
                .databaseBuilder(app, RickMortyDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration() // get correct db version if schema changed
                .build()
    }

    @Singleton
    @Provides
    fun provideCharacterDao(db: RickMortyDatabase): CharacterModelDao {
        return db.characterModelDao
    }

    @Singleton
    @Provides
    fun provideLocationDao(db: RickMortyDatabase): LocationModelDao {
        return db.locationModelDao
    }

    @Singleton
    @Provides
    fun provideEpisodeDao(db: RickMortyDatabase): EpisodeModelDao {
        return db.episodeModelDao
    }

    @Singleton
    @Provides
    fun provideRecentQueryDao(db: RickMortyDatabase): RecentQueryDao {
        return db.recentQueryDao
    }

}