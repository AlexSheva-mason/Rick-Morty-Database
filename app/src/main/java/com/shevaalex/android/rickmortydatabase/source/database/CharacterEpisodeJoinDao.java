package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CharacterEpisodeJoinDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert (CharacterEpisodeJoin characterEpisodeJoin);

    @Query("SELECT * FROM Character INNER JOIN CharacterEpisodeJoin ON Character.id = CharacterEpisodeJoin.characterId WHERE CharacterEpisodeJoin.episodeId=:episodeID")
    DataSource.Factory<Integer, Character> getCharactersFromEpisode(int episodeID);

    @Query("SELECT * FROM Episode INNER JOIN CharacterEpisodeJoin ON Episode.id = CharacterEpisodeJoin.episodeId WHERE CharacterEpisodeJoin.characterId=:characterID")
    DataSource.Factory<Integer, Episode> getEpisodesFromCharacters (int characterID);

}
