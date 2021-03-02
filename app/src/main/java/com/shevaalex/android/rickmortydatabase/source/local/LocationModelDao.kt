package com.shevaalex.android.rickmortydatabase.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationModelDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity?>?)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    /**
     * gets the last location to compare databases
      */
    @Query("SELECT * FROM LocationEntity ORDER BY id DESC LIMIT 1")
    suspend fun getLastInLocationTable(): LocationEntity?

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM LocationEntity")
    suspend fun locationsCount(): Int

    /**
     * gets all names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM LocationEntity")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets filtered (type and dimensions) names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM LocationEntity
        WHERE type IN (:types)
        AND dimension IN (:dimensions)""")
    fun getSuggestionsNamesTypeAndDimensFiltered(
            types: List<String>,
            dimensions: List<String>
    ): Flow<List<String>>

    /**
     * gets filtered (by type only) names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM LocationEntity
        WHERE type IN (:types)""")
    fun getSuggestionsNamesTypeFiltered(
            types: List<String>
    ): Flow<List<String>>

    /**
     * gets filtered (by dimension only) names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM LocationEntity
        WHERE dimension IN (:dimensions)""")
    fun getSuggestionsNamesDimensFiltered(
            dimensions: List<String>
    ): Flow<List<String>>

    /**
     * gets all locations
     */
    @Query("""SELECT * FROM LocationEntity
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getAllLocations(): DataSource.Factory<Int, LocationEntity>

    /**
     * performs a search by location's name in the database, shows all results
     */
    @Query("""SELECT * FROM LocationEntity
        WHERE name LIKE '%' || :name || '%'
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchLocations(name: String): DataSource.Factory<Int, LocationEntity>

    /**
     * gets filtered TYPE and DIMENSION
     */
    @Query("""SELECT * FROM LocationEntity
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND type IN (:types)
        AND dimension IN (:dimensions)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredTypeAndDimensionLocations(
            name: String? = null,
            types: List<String>,
            dimensions: List<String>
    ): DataSource.Factory<Int, LocationEntity>

    /**
     * gets filtered TYPE only
     */
    @Query("""SELECT * FROM LocationEntity
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND type IN (:types)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredTypeLocations(
            name: String? = null,
            types: List<String>
    ): DataSource.Factory<Int, LocationEntity>

    /**
     * gets filtered DIMENSION only
     */
    @Query("""SELECT * FROM LocationEntity
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND dimension IN (:dimensions)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun searchFilteredDimensionLocations(
            name: String? = null,
            dimensions: List<String>
    ): DataSource.Factory<Int, LocationEntity>

    /**
     * gets a location by ID
     */
    @Query("""SELECT * FROM LocationEntity
        WHERE id = :id
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getLocationById(id: Int): LiveData<LocationEntity>

    /**
     * gets a location with a provided id
     */
    @Query("SELECT * FROM LocationEntity WHERE id = :id")
    suspend fun getLocationByIdSuspend(id: Int): LocationEntity?

}