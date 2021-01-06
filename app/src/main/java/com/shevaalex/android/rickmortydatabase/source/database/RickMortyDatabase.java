package com.shevaalex.android.rickmortydatabase.source.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.RecentQuery;
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel;
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel;

@Database(entities = {
        CharacterModel.class,
        LocationModel.class,
        EpisodeModel.class,
        RecentQuery.class
}, version = 3)
@TypeConverters({Converters.class})
public abstract class RickMortyDatabase extends RoomDatabase {

    public abstract CharacterModelDao getCharacterModelDao();
    public abstract LocationModelDao getLocationModelDao();
    public abstract EpisodeModelDao getEpisodeModelDao();
    public abstract RecentQueryDao getRecentQueryDao();

}
