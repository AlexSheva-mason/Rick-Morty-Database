package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CharacterEpisodeJoinDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert (CharacterEpisodeJoin characterEpisodeJoin);

    @Query("SELECT * FROM Character INNER JOIN CharacterEpisodeJoin ON Character.id = CharacterEpisodeJoin.characterId WHERE CharacterEpisodeJoin.episodeId=:episodeID")
    List<Character> getCharactersFromEpisodes (int episodeID);

    @Query("SELECT * FROM Episode INNER JOIN CharacterEpisodeJoin ON Episode.id = CharacterEpisodeJoin.episodeId WHERE CharacterEpisodeJoin.characterId=:characterID")
    List<Episode> getEpisodesFromCharacters (int characterID);

}
