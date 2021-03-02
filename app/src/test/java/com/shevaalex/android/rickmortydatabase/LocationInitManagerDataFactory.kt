package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.location.LocationEntity

class LocationInitManagerDataFactory : DataFactory<LocationEntity>() {

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