package com.shevaalex.android.rickmortydatabase.source.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel

@Dao
interface CharacterModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterModel?>?)

    // gets the last character to compare databases
    @Query("SELECT * FROM CharacterModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInCharacterTable(): CharacterModel

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM CharacterModel")
    suspend fun charactersCount(): Int

    //get all characters
    /*@get:Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    val characterList: LiveData<List<CharacterModel?>?>?*/

    /*@Query("SELECT * FROM CharacterModel WHERE id LIKE :id")
    fun getCharacterById(id: Int): LiveData<CharacterModel?>?*/

    //gets a paged list of all characters
    /*@get:Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    val characterPagedList: DataSource.Factory<Int?, CharacterModel?>?*/

    //performs a search by character's name in the database, shows all results
    /*@Query("SELECT id, name, status, species, gender, originLocation, lastLocation, imageUrl," +
            "episodeList, timeStamp FROM CharacterModel WHERE name LIKE :name " +
            "ORDER BY LENGTH(episodeList) DESC, name COLLATE LOCALIZED")
    fun getCharacterList(name: String?): DataSource.Factory<Int?, CharacterModel?>?*/

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