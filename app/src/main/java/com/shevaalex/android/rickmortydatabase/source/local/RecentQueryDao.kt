package com.shevaalex.android.rickmortydatabase.source.local

import androidx.room.*
import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import com.shevaalex.android.rickmortydatabase.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface RecentQueryDao {

    @Insert
    suspend fun saveQuery(query: RecentQuery)

    @Delete
    suspend fun deleteQuery(query: RecentQuery)

    /**
     * gets number of recent queries of an appropriate type
     */
    @Query("SELECT COUNT(id) FROM RecentQuery WHERE type = :type")
    suspend fun recentCount(type: String): Int

    /**
     * gets oldest query of an appropriate type
     */
    @Query("SELECT * FROM RecentQuery WHERE type = :type ORDER BY id ASC LIMIT 1")
    suspend fun getOldestSavedQuery(type: String): RecentQuery

    /**
     * gets recent queries for suggestions
     */
    @Query("SELECT name FROM RecentQuery WHERE type = :type ORDER BY id DESC")
    fun getRecentQueries(type: String): Flow<List<String>>

    /**
     * deletes a query with a specific name
     */
    @Query("DELETE FROM RecentQuery WHERE name = :name AND type = :type")
    fun deleteQuery(name: String, type: String)

    @Transaction
    suspend fun insertAndDeleteInTransaction(newQuery: RecentQuery) {
        if (recentCount(newQuery.type) >= Constants.SV_RECENT_COUNT) {
            deleteQuery(getOldestSavedQuery(newQuery.type))
        }
        //delete saved query duplicates
        deleteQuery(newQuery.name, newQuery.type)
        saveQuery(newQuery)
    }

}