package com.shevaalex.android.rickmortydatabase.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity?>?)

    @Update
    suspend fun updateEpisode(episode: EpisodeEntity)

    /**
     * gets the last episode to compare databases
     */
    @Query("SELECT * FROM EpisodeEntity ORDER BY id DESC LIMIT 1")
    suspend fun getLastInEpisodeTable(): EpisodeEntity?

    /**
     * gets the entry count to compare databases
     */
    @Query("SELECT COUNT(id) FROM EpisodeEntity")
    suspend fun episodesCount(): Int

    /**
     * gets all names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name FROM EpisodeEntity")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets filtered names for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT name FROM EpisodeEntity
        WHERE (code LIKE :seasonCode1 || '___' 
            OR code LIKE :seasonCode2 || '___'
            OR code LIKE :seasonCode3 || '___'
            OR code LIKE :seasonCode4 || '___')""")
    fun getSuggestionsNamesFiltered(
            seasonCode1: String,
            seasonCode2: String,
            seasonCode3: String,
            seasonCode4: String
    ): Flow<List<String>>

    /**
     * gets all codes for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT code FROM EpisodeEntity")
    fun getSuggestionsCodes(): Flow<List<String>>

    /**
     * gets filtered codes for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""SELECT code FROM EpisodeEntity
        WHERE (code LIKE :seasonCode1 || '___' 
            OR code LIKE :seasonCode2 || '___'
            OR code LIKE :seasonCode3 || '___'
            OR code LIKE :seasonCode4 || '___')""")
    fun getSuggestionsCodesFiltered(
            seasonCode1: String,
            seasonCode2: String,
            seasonCode3: String,
            seasonCode4: String
    ): Flow<List<String>>

    /**
     * gets all episodes
     */
    @Query("""SELECT * FROM EpisodeEntity
        ORDER BY code""")
    fun getAllEpisodes(): DataSource.Factory<Int, EpisodeEntity>

    /**
     * performs a search by episode's name or code in the database, shows all results
     */
    @Query("""SELECT * FROM EpisodeEntity
        WHERE name LIKE '%' || :name || '%'
        OR code LIKE '%' || :name || '%'
        ORDER BY code""")
    fun searchEpisodes(name: String): DataSource.Factory<Int, EpisodeEntity>

    /**
     * performs an optional search by episode's name or code
     * filters the result by seasons selected
     */
    @Query("""SELECT * FROM EpisodeEntity
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%' OR code LIKE '%' || :name || '%')
        AND (code LIKE :seasonCode1 || '___' 
            OR code LIKE :seasonCode2 || '___'
            OR code LIKE :seasonCode3 || '___'
            OR code LIKE :seasonCode4 || '___')
        ORDER BY code""")
    fun searchFilteredEpisodes(
            name: String? = null,
            seasonCode1: String,
            seasonCode2: String,
            seasonCode3: String,
            seasonCode4: String
    ): DataSource.Factory<Int, EpisodeEntity>

    /**
     * gets episodes with provided ids
     */
    @Query("""SELECT * FROM EpisodeEntity
        WHERE id IN (:idList)
        ORDER BY code""")
    fun getEpisodesByIds(idList: List<Int>): LiveData<List<EpisodeEntity>>

    /**
     * gets an episode with a provided id
     */
    @Query("SELECT * FROM EpisodeEntity WHERE id = :id")
    suspend fun getEpisodeByIdSuspend(id: Int): EpisodeEntity?

}