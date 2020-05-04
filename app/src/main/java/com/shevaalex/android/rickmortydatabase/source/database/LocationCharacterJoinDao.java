package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationCharacterJoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LocationCharacterJoin locationCharacterJoin);

    @Query("SELECT * FROM Character INNER JOIN LocationCharacterJoin ON Character.id = LocationCharacterJoin.characterId WHERE LocationCharacterJoin.locationId=:locationID")
    LiveData<List<Character>> getCharactersFromLocations(int locationID);

}
