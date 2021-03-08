package com.shevaalex.android.rickmortydatabase.source.local

import com.shevaalex.android.rickmortydatabase.models.RmObject


abstract class DataFactory<out T: RmObject> {

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
     * creates a single object T: RmObject with a given [id]
     */
    abstract fun produceObjectModel(id: Int): T

}