package com.shevaalex.android.rickmortydatabase.source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel

@Dao
interface EpisodeModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeModel?>?)

    // gets the last episode to compare databases
    @Query("SELECT * FROM EpisodeModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInEpisodeTable(): EpisodeModel

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM EpisodeModel")
    suspend fun episodesCount(): Int

}