package com.shevaalex.android.rickmortydatabase.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class FilterMediatorLiveData(
        sourceQuery: LiveData<String>,
        sourceFilter: LiveData<Map<String, Pair<Boolean, String?>>>
): MediatorLiveData<Pair<String, Map<String, Pair<Boolean, String?>>>>() {

    private var query: String = ""
    private var filter: Map<String, Pair<Boolean, String?>> = mapOf()

    init {

        addSource(sourceQuery) {
            it?.let {string ->
                query = string
                value = Pair(query, filter)
            }
        }

        addSource(sourceFilter) {
            it?.let {map ->
                filter = map
                value = Pair(query, filter)
            }
        }

    }

}