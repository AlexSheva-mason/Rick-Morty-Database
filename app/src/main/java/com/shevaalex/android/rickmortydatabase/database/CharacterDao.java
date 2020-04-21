package com.shevaalex.android.rickmortydatabase.database;


import androidx.lifecycle.LiveData;
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

    //perform a search by character's name in the database, excluding Dead
    @Query("SELECT * FROM Character WHERE name LIKE :name AND (status LIKE 'alive' OR status LIKE 'unknown') ORDER BY name")
    DataSource.Factory<Integer, Character> searchInCharacterListNoDead(String name);

    //shows list of all characters
    @Query("SELECT * FROM Character ORDER BY name")
    DataSource.Factory<Integer, Character> showAllCharacters();

    //shows list of all characters with status Alive or Unknown
    @Query("SELECT * FROM Character WHERE status LIKE 'alive' OR status LIKE 'unknown' ORDER BY name")
    DataSource.Factory<Integer, Character> showAllCharsNoDead();

    @Query("SELECT id FROM Character ORDER BY id DESC LIMIT 1")
    Character showLastInCharacterList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacter(Character character);
}
