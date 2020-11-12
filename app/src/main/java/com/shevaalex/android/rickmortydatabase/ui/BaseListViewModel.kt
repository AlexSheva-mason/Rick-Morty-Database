package com.shevaalex.android.rickmortydatabase.ui

import android.os.Parcelable
import androidx.lifecycle.*
import timber.log.Timber

abstract class BaseListViewModel: ViewModel() {

    abstract val suggestions: LiveData<List<String>>

    abstract val recentQueries: LiveData<List<String>>

    private val _rvListPosition = MutableLiveData<Parcelable>()
    val rvListPosition: LiveData<Parcelable>
        get() = _rvListPosition

    private val loggedQueryList = MutableLiveData(mutableSetOf(" "))

    protected val _searchQuery = MutableLiveData("")
    val searchQuery: String? get() = _searchQuery.value

    protected abstract val filterData: MutableLiveData<Map<String, Pair<Boolean, String?>>>

    protected abstract val mediatorLiveData: MediatorLiveData<Pair<String, Map<String, Pair<Boolean, String?>>>>

    /**
     * saves RV list position to liveData
     */
    fun setLayoutManagerState(parcelable: Parcelable?) {
        _rvListPosition.value = parcelable
    }

    /**
     * sets the name query and resets the recyclerview list position
     */
    fun setNameQuery(name: String) {
        Timber.v("setNameQuery: %s", name)
        _rvListPosition.value = null
        _searchQuery.value = name
    }

    /**
     * sets the filtering params and resets the recyclerview list position
     */
    fun setFilterFlags(filterMap: Map<String, Pair<Boolean, String?>>) {
        if (filterData.value != filterMap) {
            _rvListPosition.value = null
            filterData.value = filterMap
        } else Timber.v("filter maps are equal")
    }

    /**
     * adds a text to list of queries
     * @return `true` -> if text has been added to existing list
     *          `false` -> if text is already present in the list (or MutableLiveData list is null)
     */
    fun addLogQuery(text: String): Boolean {
        return loggedQueryList.value?.add(text)?: false
    }

    /**
     * saves search query to db table for recent suggestions
     */
    abstract suspend fun saveSearchQuery(query: String)

    abstract fun showsAll(): Boolean

}