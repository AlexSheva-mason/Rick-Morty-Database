package com.shevaalex.android.rickmortydatabase.source.database

import androidx.paging.DataSource
import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeModel?>?)

    // gets the last episode to compare databases
    @Query("SELECT * FROM EpisodeModel ORDER BY id DESC LIMIT 1")
    suspend fun getLastInEpisodeTable(): EpisodeModel

    // gets the entry count to compare databases
    @Query("SELECT COUNT(id) FROM EpisodeModel")
    suspend fun episodesCount(): Int

    /**
     * gets all names and codes for search suggestions
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT name AND code FROM EpisodeModel")
    fun getSuggestionsNames(): Flow<List<String>>

    /**
     * gets all episodes
     */
    @Query("""SELECT * FROM EpisodeModel
        ORDER BY code""")
    fun getAllEpisodes(): DataSource.Factory<Int, EpisodeModel>

    /**
     * performs a search by episode's name or code in the database, shows all results
     */
    @Query("""SELECT * FROM EpisodeModel
        WHERE name LIKE '%' || :name || '%'
        OR code LIKE '%' || :name || '%'
        ORDER BY code""")
    fun searchEpisodes(name: String): DataSource.Factory<Int, EpisodeModel>

    /**
     * performs an optional search by episode's name or code
     * filters the result by seasons selected
     */
    @Query("""SELECT * FROM EpisodeModel
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%' OR code LIKE '%' || :name || '%')
        AND (:seasonCode1 IS NULL OR code LIKE :seasonCode1 || '___' 
            OR :seasonCode2 IS NULL OR code LIKE :seasonCode2 || '___'
            OR :seasonCode3 IS NULL OR code LIKE :seasonCode3 || '___'
            OR :seasonCode4 IS NULL OR code LIKE :seasonCode4 || '___')
        ORDER BY code""")
    fun searchFilteredEpisodes(
            name: String? = null,
            seasonCode1: String? = null,
            seasonCode2: String? = null,
            seasonCode3: String? = null,
            seasonCode4: String? = null
    ): DataSource.Factory<Int, EpisodeModel>

}