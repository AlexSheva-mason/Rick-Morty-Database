package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import kotlin.random.Random


abstract class DataFactory<out T: ApiObjectModel> {

    /**
     * creates a list with [numberOfObjects] T objects and random ids
     */
    fun createRandomIdObjectList(numberOfObjects: Int): List<T> {
        val list = mutableListOf<T>()
        for (i in 1..numberOfObjects) {
            var randomObject = produceObjectModel(Random.nextInt(1, 1000))
            while (list.contains(randomObject)) {
                randomObject = produceObjectModel(Random.nextInt(1, 1000))
            }
            list.add(randomObject)
        }
        return list
    }

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