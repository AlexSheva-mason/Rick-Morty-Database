package com.shevaalex.android.rickmortydatabase.source.local;


import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity;
import com.shevaalex.android.rickmortydatabase.models.RecentQuery;
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity;
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity;

@Database(entities = {
        CharacterEntity.class,
        LocationEntity.class,
        EpisodeEntity.class,
        RecentQuery.class
}, version = 4)
@TypeConverters({Converters.class})
public abstract class RickMortyDatabase extends RoomDatabase {

    public abstract CharacterDao getCharacterDao();
    public abstract LocationDao getLocationDao();
    public abstract EpisodeDao getEpisodeDao();
    public abstract RecentQueryDao getRecentQueryDao();

}
