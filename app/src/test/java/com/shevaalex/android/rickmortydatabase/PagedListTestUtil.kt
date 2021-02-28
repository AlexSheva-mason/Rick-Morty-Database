package com.shevaalex.android.rickmortydatabase

/**
 * Author: saied89
 * https://github.com/saied89/DVDPrism/blob/master/app/src/androidTest/java/com/saied/dvdprism/app/PagedListMock.kt
 */

import android.database.Cursor
import androidx.paging.DataSource
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.paging.LimitOffsetDataSource
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock

fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
        object : DataSource.Factory<Int, T>() {
            override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
        }

private val mockQuery = mock<RoomSQLiteQuery> {
    on { sql }.doReturn("")
}

private val mockDb = mock<RoomDatabase> {
    on { invalidationTracker }.doReturn(mock())
}

class MockLimitDataSource<T>(private val itemList: List<T>) : LimitOffsetDataSource<T>(mockDb, mockQuery, false, null) {
    override fun convertRows(cursor: Cursor?): MutableList<T> = itemList.toMutableList()
    override fun countItems(): Int = itemList.count()
    override fun isInvalid(): Boolean = false
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) { /* Not implemented */
    }

    override fun loadRange(startPosition: Int, loadCount: Int) =
            itemList.subList(startPosition, startPosition + loadCount).toMutableList()

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(itemList, 0, countItems())
    }
}