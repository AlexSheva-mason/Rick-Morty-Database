package com.shevaalex.android.rickmortydatabase

import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import kotlin.random.Random


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
     * creates a single object T: ApiObjectModel with a given [id]
     */
    abstract fun produceObjectModel(id: Int): T

}