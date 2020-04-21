package com.shevaalex.android.rickmortydatabase.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Character.class}, version = 1, exportSchema = false)
public abstract class RickMortyDatabase extends RoomDatabase {
    private static final String LOG_TAG = RickMortyDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "rmdatabase";
    private static RickMortyDatabase sInstance;

    // return a database object using Singleton pattern
    public static RickMortyDatabase getInstance(Context context){
        if (sInstance == null) {
            /*Synchronized statement. This part of the method is synchronized.
              Synchronized statement specifies the object that provides the intrinsic lock (LOCK)
            */
                synchronized (LOCK) {
                    Log.d(LOG_TAG, "Creating a new database instance");
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), RickMortyDatabase.class,
                            DATABASE_NAME).build();
                }
        } else {
            Log.d(LOG_TAG, "Getting a previous database instance");
        }
        return sInstance;
    }

    public abstract CharacterDao getCharacterDao();

}
