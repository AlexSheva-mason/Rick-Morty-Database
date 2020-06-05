package com.shevaalex.android.rickmortydatabase.source.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Character.class, Location.class, Episode.class, CharacterEpisodeJoin.class, LocationCharacterJoin.class}, version = 1, exportSchema = false)
public abstract class RickMortyDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "rmdatabase";
    private static volatile RickMortyDatabase sInstance;

    // return a database object using Singleton pattern
    public static RickMortyDatabase getInstance(Context context){
        if (sInstance == null) {
            /*Synchronized statement. This part of the method is synchronized.
              Synchronized statement specifies the object that provides the intrinsic lock (LOCK)
            */
                synchronized (LOCK) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(context.getApplicationContext(), RickMortyDatabase.class,
                                DATABASE_NAME).build();
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

}
