package com.shevaalex.android.rickmortydatabase

import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult


abstract class DataFactory<out T : ApiObjectModel> {

    /**
     * creates a list with [numberOfObjects] T objects and fixed ids
     * ids range is 1 to [numberOfObjects]
     */
    fun createFixedIdObjectList(numberOfObjects: Int): List<T> {
        val list = mutableListOf<T>()
        for (i in 1..numberOfObjects) {
            list.add(produceObjectModel(i))
        }
        return list
    }

    /**
     * returns ApiResult.Success<JsonObject> with count 50
     */
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

    /**
     * returns ApiResult.Success<List<T>> with 50 objects
     */
    fun produceApiResultListSuccess(): ApiResult<List<T>> {
        return ApiResult.Success(createFixedIdObjectList(50))
    }

    /**
     * creates a single object T: ApiObjectModel with a given [id]
     */
    abstract fun produceObjectModel(id: Int): T

}