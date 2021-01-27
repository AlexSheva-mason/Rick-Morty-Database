package com.shevaalex.android.rickmortydatabase.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterModel?>?)

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM CharacterModel")
    suspend fun charactersCount(): Int

    /**
     * gets all names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM CharacterModel")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets filtered names for search suggestions (species filtered)
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM CharacterModel
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
    @Query("""SELECT name FROM CharacterModel
        WHERE status IN (:statuses)
        AND gender IN (:genders)""")
    fun getSuggestionsNamesFiltered(
            statuses: List<String>,
            genders: List<String>
    ): Flow<List<String>>

    /**
     * gets all characters
     */
    @Query("""SELECT * FROM CharacterModel
        ORDER BY LENGTH(episodeList) DESC,
        name 
        COLLATE LOCALIZED""")
    fun getAllCharacters(): DataSource.Factory<Int, CharacterModel>

    /**
     * performs a search by character's name in the database, shows all results
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE name LIKE '%' || :name || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchCharacters(name: String?): DataSource.Factory<Int, CharacterModel>

    /**
     * performs a search by character's name with query that contains two words, queries both options
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE name LIKE '%' || :name || '%'
        OR name LIKE '%' || :nameReversed || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun searchCharacters(name: String, nameReversed: String): DataSource.Factory<Int, CharacterModel>

    /**
     * gets filtered result with filtered species, without search
     */
    @Query("""SELECT * FROM CharacterModel
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
    ): DataSource.Factory<Int, CharacterModel>

    /**
     * gets filtered result, species NOT filtered, without search
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE status IN (:statuses)
        AND gender IN (:genders)
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun getFilteredNoSpeciesCharacters(
            statuses: List<String>,
            genders: List<String>
    ): DataSource.Factory<Int, CharacterModel>

    /**
     * searches and gets filtered result with filtered species
     */
    @Query("""SELECT * FROM CharacterModel
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
    ): DataSource.Factory<Int, CharacterModel>

    /**
     * searches and gets filtered result, species NOT filtered
     */
    @Query("""SELECT * FROM CharacterModel
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
    ): DataSource.Factory<Int, CharacterModel>

    /**
     * gets characters with provided ids
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE id IN (:idList)
        ORDER BY name
        COLLATE LOCALIZED""")
    fun getCharactersByIds(idList: List<Int>): LiveData<List<CharacterModel>>

    /**
     * gets a character with a provided id
     */
    @Query("SELECT * FROM CharacterModel WHERE id = :id")
    suspend fun getCharacterByIdSuspend(id: Int): CharacterModel?

}