package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LocationCharacterJoinDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertLocationCharacterJoinList (List<LocationCharacterJoin> locationCharacterJoins);

    @Query("SELECT id, name, status, species, gender, lastKnownLocation, imgUrl FROM Character INNER JOIN LocationCharacterJoin ON Character.id = LocationCharacterJoin.characterId WHERE LocationCharacterJoin.locationId=:locationID")
    LiveData<List<CharacterSmall>> getCharactersFromLocations(int locationID);

}
