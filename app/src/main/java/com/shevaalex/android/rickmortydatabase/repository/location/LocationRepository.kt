package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.ListRepository

interface LocationRepository : ListRepository {

    fun getAllLocations(): DataSource.Factory<Int, LocationModel>

    fun searchAndFilterLocations(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, LocationModel>

}