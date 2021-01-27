package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterDataFactory
@Inject constructor(): DataFactory<CharacterModel>() {

    fun produceCharacterModel(id: Int): CharacterModel {
        return CharacterModel(
                id = id,
                name = "testName$id",
                status = "testStatus$id",
                species = "testSpecies$id",
                gender = "testGender$id",
                originLocation = produceLinkedLocation(id),
                lastLocation = produceLinkedLocation(id),
                imageUrl = "testImageUrl$id",
                listOf()
        )
    }

    /**
     * creates a single LinkedLocationModel with a given [id]
     */
    private fun produceLinkedLocation(id: Int): LinkedLocationModel {
        return LinkedLocationModel("testName$id", "testUrl$id")
    }

    override fun produceObjectModel(id: Int) = produceCharacterModel(id)

}