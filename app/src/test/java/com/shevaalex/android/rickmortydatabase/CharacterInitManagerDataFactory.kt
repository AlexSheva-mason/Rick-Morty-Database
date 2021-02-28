package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel

class CharacterInitManagerDataFactory : DataFactory<CharacterModel>() {

    fun produceCharacterModel(id: Int): CharacterModel {
        return CharacterModel(
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
     * creates a single LinkedLocationModel with a given [id]
     */
    private fun produceLinkedLocation(id: Int): LinkedLocationModel {
        return LinkedLocationModel("testName$id", "testUrl/$id")
    }

    override fun produceObjectModel(id: Int) = produceCharacterModel(id)

}