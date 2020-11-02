package com.shevaalex.android.rickmortydatabase.source.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterQuery;
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel;
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel;

@Database(entities = {
        CharacterModel.class,
        LocationModel.class,
        EpisodeModel.class,
        CharacterQuery.class,
        Character.class,
        Location.class,
        Episode.class,
        CharacterEpisodeJoin.class,
        LocationCharacterJoin.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class RickMortyDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "rmdatabase";
    private static volatile RickMortyDatabase sInstance;

    public static RickMortyDatabase getInstance(Context context){
        if (sInstance == null) {
                synchronized (LOCK) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                                    RickMortyDatabase.class, DATABASE_NAME)
                                .fallbackToDestructiveMigration()
                                .build();
                    }
                }
        }
        return sInstance;
    }

    public abstract CharacterDao getCharacterDao();
    public abstract LocationDao getLocationDao();
    public abstract EpisodeDao getEpisodeDao();
    public abstract CharacterEpisodeJoinDao getCharacterEpisodeJoinDao();
    public abstract LocationCharacterJoinDao getLocationCharacterJoinDao();
    public abstract CharacterModelDao getCharacterModelDao();
    public abstract LocationModelDao getLocationModelDao();
    public abstract EpisodeModelDao getEpisodeModelDao();
    public abstract CharacterRecentDao getCharacterRecentDao();

}
