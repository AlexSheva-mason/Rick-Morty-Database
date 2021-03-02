package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.character.CharacterEntity
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterDataFactory
@Inject constructor(): DataFactory<CharacterEntity>() {

    fun produceCharacterEntity(id: Int): CharacterEntity {
        return CharacterEntity(
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
     * creates a single LinkedLocation with a given [id]
     */
    private fun produceLinkedLocation(id: Int): LinkedLocation {
        return LinkedLocation("testName$id", "testUrl$id")
    }

    override fun produceObjectModel(id: Int) = produceCharacterEntity(id)

}