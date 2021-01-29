package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.RecentQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentQueryDataFactory @Inject constructor() {

    private val types = listOf(
            RecentQuery.Type.CHARACTER, RecentQuery.Type.LOCATION, RecentQuery.Type.EPISODE
    )

    fun produceRecentWithSpecificType(id: Int, type: String): RecentQuery {
        return RecentQuery(
                id = id,
                name = "testName$id",
                type = type
        )
    }

    fun produceRecent(id: Int): RecentQuery {
        return RecentQuery(
                id = id,
                name = "testName$id",
                type = types.random().type
        )
    }

}