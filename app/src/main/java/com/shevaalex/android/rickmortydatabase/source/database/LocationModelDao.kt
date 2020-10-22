package com.shevaalex.android.rickmortydatabase.source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel

@Dao
interface LocationModelDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertLocations(locations: List<LocationModel?>?)

    // gets the last location to compare databases
    @Query("SELECT * FROM LocationModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInLocationTable(): LocationModel

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM LocationModel")
    suspend fun locationsCount(): Int

}