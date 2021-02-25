package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.location.LocationModel

class LocationInitManagerDataFactory : DataFactory<LocationModel>() {

    override fun produceObjectModel(id: Int): LocationModel {
        return LocationModel(
                id = id,
                name = "testName$id",
                type = "testType$id",
                dimension = "testDimension$id",
                imageUrl = "testImageUrl$id",
                characters = listOf()
        )
    }

}