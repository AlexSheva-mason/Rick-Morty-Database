package com.shevaalex.android.rickmortydatabase.repository.location

import androidx.paging.DataSource
import com.shevaalex.android.rickmortydatabase.LocationInitManagerDataFactory
import com.shevaalex.android.rickmortydatabase.createMockDataSourceFactory
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.repository.BaseListRepository


class FakeLocationRepository : BaseListRepository(), LocationRepository {

    private val dataFactory = LocationInitManagerDataFactory()

    val allLocations = dataFactory.createFixedIdObjectList(100)
    val filteredLocations = dataFactory.createFixedIdObjectList(50)

    override fun getAllLocations(): DataSource.Factory<Int, LocationModel> {
        return createMockDataSourceFactory(allLocations)
    }

    override fun searchAndFilterLocations(
            query: String,
            filterMap: Map<String, Pair<Boolean, String?>>,
            showsAll: Boolean
    ): DataSource.Factory<Int, LocationModel> {
        return createMockDataSourceFactory(filteredLocations)
    }

}