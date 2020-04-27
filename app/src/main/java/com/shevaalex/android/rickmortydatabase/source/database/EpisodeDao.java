package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EpisodeDao {
    //shows list of all episodes
    @Query("SELECT * FROM Episode ORDER BY name")
    DataSource.Factory<Integer, Episode> showAllEpisodes();

    // gets the last episode to compare databases
    @Query("SELECT * FROM Episode ORDER BY id DESC LIMIT 1")
    Episode showLastInEpisodeList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisode(Episode episode);
}
