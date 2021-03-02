package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shevaalex.android.rickmortydatabase.DataFactory
import com.shevaalex.android.rickmortydatabase.models.RmObject
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult

class FakeInitManager<T : RmObject>(
        dataFactory: DataFactory<T>
) : InitManager<T> {

    private val sharedPref: SharedPreferences = mock()
    private val sharedPrefEditor: SharedPreferences.Editor = mock()
    private val apiResultCountSuccess = dataFactory.produceApiResultCountSuccess()
    private val apiResultListSuccess = dataFactory.produceApiResultListSuccess()
    private val apiResultFailure = dataFactory.produceApiResultFailure()
    private val dbCount = 50
    private var shouldReturnNetworkError = false

    init {
        whenever(sharedPref.edit()).thenReturn(sharedPrefEditor)
        whenever(sharedPref.getInt(any(), any())).thenReturn(0)
    }

    fun setShouldReturnNetworkError(bool: Boolean) {
        shouldReturnNetworkError = bool
    }

    override fun getSharedPrefsKey(): String = "fake_key"

    override suspend fun getNetworkCountApiResult(token: String): ApiResult<JsonObject> {
        return if (shouldReturnNetworkError) apiResultFailure
        else apiResultCountSuccess
    }

    override suspend fun getListFromNetwork(token: String): ApiResult<List<T?>> {
        return if (shouldReturnNetworkError) apiResultFailure
        else apiResultListSuccess
    }

    override suspend fun getObjectCountDb(): Int {
        return dbCount
    }

    override suspend fun filterNetworkList(networkList: List<T>): List<T> {
        return listOf()
    }

    override suspend fun saveNetworkListToDb(networkList: List<T>) {}

    override fun getSharedPrefs(): SharedPreferences {
        return sharedPref
    }

}