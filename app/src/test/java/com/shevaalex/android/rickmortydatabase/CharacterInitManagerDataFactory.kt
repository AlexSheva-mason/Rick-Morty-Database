package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocation

class CharacterInitManagerDataFactory : DataFactory<CharacterEntity>() {

    fun produceCharacterEntity(id: Int): CharacterEntity {
        return CharacterEntity(
                id = id,
                name = "testName$id",
                status = "testStatus$id",
                species = "testSpecies$id",
                gender = "testGender$id",
                originLocation = produceLinkedLocation(id*2),
                lastLocation = produceLinkedLocation(id*3),
                imageUrl = "testImageUrl$id",
                listOf(
                        (id*2).toString(),
                        (id*3).toString(),
                        (id*4).toString()
                )
        )
    }

    /**
     * creates a single LinkedLocation with a given [id]
     */
    private fun produceLinkedLocation(id: Int): LinkedLocation {
        return LinkedLocation("testName$id", "testUrl/$id")
    }

    override fun produceObjectModel(id: Int) = produceCharacterEntity(id)

}