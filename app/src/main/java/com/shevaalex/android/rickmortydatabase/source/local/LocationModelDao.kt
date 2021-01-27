package com.shevaalex.android.rickmortydatabase.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationModelDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertLocations(locations: List<LocationModel?>?)

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM LocationModel")
    suspend fun locationsCount(): Int

    /**
     * gets all names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM LocationModel")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets filtered (type and dimensions) names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM LocationModel
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
    @Query("""SELECT name FROM LocationModel
        WHERE type IN (:types)""")
    fun getSuggestionsNamesTypeFiltered(
            types: List<String>
    ): Flow<List<String>>

    /**
     * gets filtered (by dimension only) names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM LocationModel
        WHERE dimension IN (:dimensions)""")
    fun getSuggestionsNamesDimensFiltered(
            dimensions: List<String>
    ): Flow<List<String>>

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
    fun searchLocations(name: String): DataSource.Factory<Int, LocationModel>

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

    /**
     * gets a location by ID
     */
    @Query("""SELECT * FROM LocationModel
        WHERE id = :id
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getLocationById(id: Int): LiveData<LocationModel>

    /**
     * gets a location with a provided id
     */
    @Query("SELECT * FROM LocationModel WHERE id = :id")
    suspend fun getLocationByIdSuspend(id: Int): LocationModel?

}