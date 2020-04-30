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
    @Query("SELECT * FROM Character WHERE name LIKE :name ORDER BY name")
    DataSource.Factory<Integer, Character> searchInCharacterList(String name);

    //returns a Character with a given id
    @Query("SELECT * FROM Character WHERE id == :id")
    Character returnCharacterById(int id);

    //perform a search by character's name in the database, excluding Dead
    @Query("SELECT * FROM Character WHERE name LIKE :name AND (status LIKE 'alive' OR status LIKE 'unknown') ORDER BY name")
    DataSource.Factory<Integer, Character> searchInCharacterListNoDead(String name);

    //shows list of all characters
    @Query("SELECT * FROM Character ORDER BY name")
    DataSource.Factory<Integer, Character> showAllCharacters();

    //returns list of all characters
    @Query("SELECT * FROM Character ORDER BY name")
    List<Character> getAllCharacters();

    //shows list of all characters with status Alive or Unknown
    @Query("SELECT * FROM Character WHERE status LIKE 'alive' OR status LIKE 'unknown' ORDER BY name")
    DataSource.Factory<Integer, Character> showAllCharsNoDead();

    // gets the last character to compare databases
    @Query("SELECT * FROM Character ORDER BY id DESC LIMIT 1")
    Character showLastInCharacterList();

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM Character")
    int getCharacterCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacter(Character character);
}
