package com.shevaalex.android.rickmortydatabase

import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult

class CharacterInitManagerDataFactory : DataFactory<CharacterModel>() {

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

    fun produceApiResultCountSuccess(): ApiResult<JsonObject> {
        val jsonObject = JsonObject()
        for (i in 1..50) {
            jsonObject.addProperty(i.toString(), true)
        }
        return ApiResult.Success(jsonObject)
    }

    fun produceApiResultFailure(): ApiResult<Nothing> = ApiResult.Failure(null)

    fun produceApiResultNetworkError(): ApiResult<Nothing> = ApiResult.NetworkError

    fun produceApiResultEmpty(): ApiResult<Nothing> = ApiResult.Empty

    fun produceApiResultListSuccess(): ApiResult<List<CharacterModel>> {
        val characterList = createFixedIdObjectList(50)
        return ApiResult.Success(characterList)
    }

    /**
     * creates a single LinkedLocationModel with a given [id]
     */
    private fun produceLinkedLocation(id: Int): LinkedLocationModel {
        return LinkedLocationModel("testName$id", "testUrl$id")
    }

    override fun produceObjectModel(id: Int) = produceCharacterModel(id)

}