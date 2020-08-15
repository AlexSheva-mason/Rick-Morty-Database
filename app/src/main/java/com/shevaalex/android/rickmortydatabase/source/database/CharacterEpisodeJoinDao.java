package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CharacterEpisodeJoinDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertCharacterEpisodeJoinList (List<CharacterEpisodeJoin> characterEpisodeJoins);

    @Query("SELECT * " +
            "FROM Character INNER JOIN CharacterEpisodeJoin ON Character.id = CharacterEpisodeJoin.characterId " +
            "WHERE CharacterEpisodeJoin.episodeId=:episodeID ORDER BY name COLLATE LOCALIZED")
    LiveData<List<Character>> getCharactersFromEpisode(int episodeID);

    @Query("SELECT id, name, airDate, code, charactersList " +
            "FROM Episode INNER JOIN CharacterEpisodeJoin ON Episode.id = CharacterEpisodeJoin.episodeId " +
            "WHERE CharacterEpisodeJoin.characterId=:characterID")
    LiveData<List<Episode>> getEpisodesFromCharacters (int characterID);

}
