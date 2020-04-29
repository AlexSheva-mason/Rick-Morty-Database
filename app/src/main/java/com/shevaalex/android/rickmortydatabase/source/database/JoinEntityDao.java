package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface JoinEntityDao {
    @Insert
    void insert (JoinEntity joinEntity);

    @Query("SELECT * FROM Character INNER JOIN JoinEntity ON Character.id = JoinEntity.characterId WHERE JoinEntity.locationId=:locationID")
    List<Character> getCharactersFromLocations (int locationID);

    @Query("SELECT * FROM Character INNER JOIN JoinEntity ON Character.id = JoinEntity.characterId WHERE JoinEntity.episodeId=:episodeID")
    List<Character> getCharactersFromEpisodes (int episodeID);

    @Query("SELECT * FROM Episode INNER JOIN JoinEntity ON Episode.id = JoinEntity.episodeId WHERE JoinEntity.characterId=:characterID")
    List<Episode> getEpisodesFromCharacters (int characterID);

}
