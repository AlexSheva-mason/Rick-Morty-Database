package com.shevaalex.android.rickmortydatabase.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity?>?)

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM CharacterEntity")
    suspend fun charactersCount(): Int

    /**
     * gets all names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM CharacterEntity")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets filtered names for search suggestions (species filtered)
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM CharacterEntity
        WHERE status IN (:statuses)
        AND gender IN (:genders)
        AND species IN (:species)""")
    fun getSuggestionsNamesFiltered(
            statuses: List<String>,
            genders: List<String>,
            species: List<String>
    ): Flow<List<String>>

    /**
     * gets filtered names for search suggestions (species show ALL)
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM CharacterEntity
        WHERE status IN (:statuses)
        AND gender IN (:genders)""")
    fun getSuggestionsNamesFiltered(
            statuses: List<String>,
            genders: List<String>
    ): Flow<List<String>>

    /**
     * gets all characters
     */
    @Query("""SELECT * FROM CharacterEntity
        ORDER BY LENGTH(episodeList) DESC,
        name 
        COLLATE LOCALIZED""")
    fun getAllCharacters(): DataSource.Factory<Int, CharacterEntity>

    /**
     * performs a search by character's name in the database, shows all results
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE name LIKE '%' || :name || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchCharacters(name: String?): DataSource.Factory<Int, CharacterEntity>

    /**
     * performs a search by character's name with query that contains two words, queries both options
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE name LIKE '%' || :name || '%'
        OR name LIKE '%' || :nameReversed || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchCharacters(name: String, nameReversed: String): DataSource.Factory<Int, CharacterEntity>

    /**
     * gets filtered result with filtered species, without search
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE status IN (:statuses)
        AND gender IN (:genders)
        AND species IN (:species)
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun getFilteredCharacters(
            statuses: List<String>,
            genders: List<String>,
            species: List<String>
    ): DataSource.Factory<Int, CharacterEntity>

    /**
     * gets filtered result, species NOT filtered, without search
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE status IN (:statuses)
        AND gender IN (:genders)
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun getFilteredNoSpeciesCharacters(
            statuses: List<String>,
            genders: List<String>
    ): DataSource.Factory<Int, CharacterEntity>

    /**
     * searches and gets filtered result with filtered species
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE name LIKE '%' || :name || '%'
        AND status IN (:statuses)
        AND gender IN (:genders)
        AND species IN (:species)
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchAndFilterCharacters(
            name: String,
            statuses: List<String>,
            genders: List<String>,
            species: List<String>
    ): DataSource.Factory<Int, CharacterEntity>

    /**
     * searches and gets filtered result, species NOT filtered
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE name LIKE '%' || :name || '%'
        AND status IN (:statuses)
        AND gender IN (:genders)
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchAndFilterNoSpeciesCharacters(
            name: String,
            statuses: List<String>,
            genders: List<String>
    ): DataSource.Factory<Int, CharacterEntity>

    /**
     * gets characters with provided ids
     */
    @Query("""SELECT * FROM CharacterEntity
        WHERE id IN (:idList)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getCharactersByIds(idList: List<Int>): LiveData<List<CharacterEntity>>

    /**
     * gets a character with a provided id
     */
    @Query("SELECT * FROM CharacterEntity WHERE id = :id")
    suspend fun getCharacterByIdSuspend(id: Int): CharacterEntity?

}