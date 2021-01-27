package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CharacterDataFactory @Inject constructor() {

    /**
     * creates a list with [numberOfCharacters] CharacterModels and random ids
     */
    fun createListOfRandomCharacters(numberOfCharacters: Int): List<CharacterModel> {
        val list = mutableListOf<CharacterModel>()
        for (i in 1..numberOfCharacters) {
            var randomCharacter = produceCharacterModel(Random.nextInt(1, Integer.MAX_VALUE))
            while (list.contains(randomCharacter)) {
                randomCharacter = produceCharacterModel(Random.nextInt(1, Integer.MAX_VALUE))
            }
            list.add(randomCharacter)
        }
        return list
    }

    /**
     * creates a list with [numberOfCharacters] CharacterModels and fixed ids
     * ids range is 1 to [numberOfCharacters]
     */
    fun createListOfFixedCharacters(numberOfCharacters: Int): List<CharacterModel> {
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