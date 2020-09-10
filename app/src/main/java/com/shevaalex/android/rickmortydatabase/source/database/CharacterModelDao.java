package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;

import java.util.List;

@Dao
public interface CharacterModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacterList(List<CharacterModel> characters);

    //get all characters
    @Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    LiveData<List<CharacterModel>> getCharacterList();

    @Query("SELECT * FROM CharacterModel WHERE id LIKE :id")
    LiveData<CharacterModel> getCharacterById(int id);

    //gets a paged list of all characters
    @Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    DataSource.Factory<Integer, CharacterModel> getCharacterPagedList();

    //performs a search by character's name in the database, shows all results
    @Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel WHERE name LIKE :name " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    DataSource.Factory<Integer, CharacterModel> getCharacterList(String name);

    //performs a search by character's name in the database, excluding Dead
    @Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel WHERE name LIKE :name" +
            " AND (status IN (:notDeadStatus)) ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    DataSource.Factory<Integer, CharacterModel> getCharacterList(String name, String[] notDeadStatus);

    //shows list of all characters, excluding Dead
    @Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "WHERE status IN (:notDeadStatus) ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    DataSource.Factory<Integer, CharacterModel> getCharacterList(String[] notDeadStatus);

    // gets the last character to compare databases
    @Query("SELECT * FROM CharacterModel ORDER BY id DESC LIMIT 1")
    CharacterModel showLastInCharacterList();

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM CharacterModel")
    int getCharacterCount();

}
