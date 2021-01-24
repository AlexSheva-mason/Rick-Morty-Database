package com.shevaalex.android.rickmortydatabase.di

import androidx.room.Room
import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.source.local.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object TestDbModule{

    @Singleton
    @Provides
    fun provideAppDb(app: TestRmApplication): RickMortyDatabase {
        return Room
                .inMemoryDatabaseBuilder(
                        app,
                        RickMortyDatabase::class.java
                )
                .allowMainThreadQueries()
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