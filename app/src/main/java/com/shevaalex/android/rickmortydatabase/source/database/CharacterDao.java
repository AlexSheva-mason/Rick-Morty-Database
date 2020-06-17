package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface CharacterDao {
    //perform a search by character's name in the database, shows all results
    @Query("SELECT id, name, status, species, gender, lastKnownLocation, imgUrl FROM Character WHERE name LIKE :name ORDER BY LENGTH(episodeList) DESC, name")
    DataSource.Factory<Integer, CharacterSmall> searchInCharacterList(String name);

    //perform a search by character's name in the database, excluding Dead
    @Query("SELECT id, name, status, species, gender, lastKnownLocation, imgUrl FROM Character WHERE name LIKE :name AND (status LIKE 'alive' OR status LIKE 'unknown') ORDER BY LENGTH(episodeList) DESC, name")
    DataSource.Factory<Integer, CharacterSmall> searchInCharacterListNoDead(String name);

    //shows list of all characters
    @Query("SELECT id, name, status, species, gender, lastKnownLocation, imgUrl FROM Character ORDER BY LENGTH(episodeList) DESC, name")
    DataSource.Factory<Integer, CharacterSmall> showAllCharacters();

    //shows list of all characters with status Alive or Unknown
    @Query("SELECT id, name, status, species, gender, lastKnownLocation, imgUrl FROM Character WHERE status LIKE 'alive' OR status LIKE 'unknown' ORDER BY LENGTH(episodeList) DESC, name")
    DataSource.Factory<Integer, CharacterSmall> showAllCharsNoDead();

    // gets the last character to compare databases
    @Query("SELECT * FROM Character ORDER BY id DESC LIMIT 1")
    Character showLastInCharacterList();

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM Character")
    int getCharacterCount();

    //gets the character by ID
    @Query("SELECT * FROM Character WHERE id LIKE :id")
    Character getCharacterById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacterList(List<Character> characters);
}
