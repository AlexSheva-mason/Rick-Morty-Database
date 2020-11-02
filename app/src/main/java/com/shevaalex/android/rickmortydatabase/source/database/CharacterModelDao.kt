package com.shevaalex.android.rickmortydatabase.source.database

import androidx.paging.DataSource
import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterModel?>?)

    /**
     * gets the last character to compare databases
     */
    @Query("SELECT * FROM CharacterModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInCharacterTable(): CharacterModel

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM CharacterModel")
    suspend fun charactersCount(): Int

    /**
     * gets all names for seacrh suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM CharacterModel")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets all characters
     */
    @Query("SELECT * FROM CharacterModel ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    fun getAllCharacters(): DataSource.Factory<Int, CharacterModel>

    /**
     * performs a search by character's name in the database, shows all results
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE name LIKE '%' || :name || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun getCharacterList(name: String?): DataSource.Factory<Int, CharacterModel>

    /**
     * performs a search by character's name with query that contains two words, queries both options
     */
    @Query("""SELECT * FROM CharacterModel
        WHERE name LIKE '%' || :name || '%'
        OR name LIKE '%' || :nameReversed || '%'
        ORDER BY LENGTH(episodeList) DESC,
        name
        COLLATE LOCALIZED""")
    fun getCharacterList(name: String, nameReversed: String): DataSource.Factory<Int, CharacterModel>

    /*@Query("SELECT * FROM CharacterModel WHERE id LIKE :id")
    fun getCharacterById(id: Int): LiveData<CharacterModel?>?*/

    //performs a search by character's name in the database, excluding Dead
    /*@Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel WHERE name LIKE :name" +
            " AND (status IN (:notDeadStatus)) ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    fun getCharacterList(name: String?, notDeadStatus: Array<String?>?): DataSource.Factory<Int?, CharacterModel?>?*/

    //shows list of all characters, excluding Dead
    /*@Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "WHERE status IN (:notDeadStatus) ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    fun getCharacterList(notDeadStatus: Array<String?>?): DataSource.Factory<Int?, CharacterModel?>?*/
}