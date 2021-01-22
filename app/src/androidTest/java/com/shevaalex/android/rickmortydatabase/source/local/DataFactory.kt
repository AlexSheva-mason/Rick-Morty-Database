package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataFactory @Inject constructor() {


    /**
     * creates a list with [numberOfCharacters] CharacterModels
     */
    fun createListOfCharacters(numberOfCharacters: Int): List<CharacterModel> {
        val list = mutableListOf<CharacterModel>()
        for (i in 1..numberOfCharacters) {
            list.add(produceCharacterModel(i))
        }
        return list
    }

    /**
     * creates a single CharacterModel with a given [id]
     */
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

}