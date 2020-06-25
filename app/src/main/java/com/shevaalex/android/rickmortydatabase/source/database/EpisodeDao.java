package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EpisodeDao {
    //drops the table
    @Query("DELETE FROM Episode")
    void dropTable();

    //shows list of all episodes
    @Query("SELECT * FROM Episode ORDER BY code")
    DataSource.Factory<Integer, Episode> showAllEpisodes();

    // gets the last episode to compare databases
    @Query("SELECT * FROM Episode ORDER BY id DESC LIMIT 1")
    Episode showLastInEpisodeList();

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM Episode")
    int getEpisodeCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisodeList(List<Episode> characters);
}
