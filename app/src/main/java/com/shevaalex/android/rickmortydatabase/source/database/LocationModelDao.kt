package com.shevaalex.android.rickmortydatabase.source.database

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.RoomWarnings
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationModelDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertLocations(locations: List<LocationModel?>?)

    /**
     * gets the last location to compare databases
      */
    @Query("SELECT * FROM LocationModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInLocationTable(): LocationModel

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM LocationModel")
    suspend fun locationsCount(): Int

    /**
     * gets all names for seacrh suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM LocationModel")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets all locations
     */
    @Query("""SELECT * FROM LocationModel
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getAllLocations(): DataSource.Factory<Int, LocationModel>

    /**
     * performs a search by location's name in the database, shows all results
     */
    @Query("""SELECT * FROM LocationModel
        WHERE name LIKE '%' || :name || '%'
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchLocations(name: String?): DataSource.Factory<Int, LocationModel>

    /**
     * gets filtered TYPE and DIMENSION
     */
    @Query("""SELECT * FROM LocationModel
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND type IN (:types)
        AND dimension IN (:dimensions)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredTypeAndDimensionLocations(
            name: String? = null,
            types: List<String>,
            dimensions: List<String>
    ): DataSource.Factory<Int, LocationModel>

    /**
     * gets filtered TYPE only
     */
    @Query("""SELECT * FROM LocationModel
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND type IN (:types)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredTypeLocations(
            name: String? = null,
            types: List<String>
    ): DataSource.Factory<Int, LocationModel>

    /**
     * gets filtered DIMENSION only
     */
    @Query("""SELECT * FROM LocationModel
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND dimension IN (:dimensions)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredDimensionLocations(
            name: String? = null,
            dimensions: List<String>
    ): DataSource.Factory<Int, LocationModel>

}