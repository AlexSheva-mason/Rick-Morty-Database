package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataFactory
@Inject constructor(): DataFactory<LocationEntity>() {

    override fun produceObjectModel(id: Int): LocationEntity {
        return LocationEntity(
                id = id,
                name = "testName$id",
                type = "testType$id",
                dimension = "testDimension$id",
                imageUrl = "testImageUrl$id",
                characters = listOf()
        )
    }

}