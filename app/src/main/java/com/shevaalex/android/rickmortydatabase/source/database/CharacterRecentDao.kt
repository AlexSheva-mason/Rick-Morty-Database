package com.shevaalex.android.rickmortydatabase.source.database

import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.character.CharacterQuery
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface CharacterRecentDao {

    @Insert
    suspend fun saveQuery(query: CharacterQuery)

    @Delete
    suspend fun deleteQuery(query: CharacterQuery)

    @Query("SELECT COUNT(id) FROM CharacterQuery")
    suspend fun recentCount(): Int

    @Query("SELECT * FROM CharacterQuery ORDER BY id ASC LIMIT 1")
    suspend fun getOldestSavedQuery(): CharacterQuery

    /**
     * gets recent queries for suggestions
     */
    @Query("SELECT name FROM CharacterQuery ORDER BY id DESC")
    fun getRecentQueries(): Flow<List<String>>

    @Transaction
    suspend fun insertAndDeleteInTransaction(newQuery: CharacterQuery) {
        if (recentCount() >= Constants.SV_RECENT_COUNT) {
            deleteQuery(getOldestSavedQuery())
        }
        saveQuery(newQuery)
    }

}