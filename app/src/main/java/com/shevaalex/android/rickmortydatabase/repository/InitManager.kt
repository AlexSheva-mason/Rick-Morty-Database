package com.shevaalex.android.rickmortydatabase.repository

import android.content.Context
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.models.ApiPageModel
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.UiTranslateUtils
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import java.util.*

abstract class InitManager<ObjectModel: ApiObjectModel, PageModelObject: ApiPageModel>(
        val context: Context
) {

    /**
     * Returns a StateResource object representing a status of database (eg error / success)
     */
    suspend fun getDbState(): StateResource {
        //call the first api page
        when (val apiSourceFirstPage = createCall()) {
            //if response is successfull -> get the last object's id and fetch it
            is ApiResult.Success<PageModelObject> -> {
                apiSourceFirstPage.data.apiPageInfoModel.let { pageInfoModel ->
                    val apiSourcelastObject = callLastApiModel(pageInfoModel.count)
                    // if ApiResponse with last object is successfull - check if shouldFetch
                    if (apiSourcelastObject is ApiResult.Success<ObjectModel>) {
                        val lastNetworkObject = apiSourcelastObject.data
                        val lastCacheObject = getLastEntryFromDb()
                        val dbObjectsCount = getDbEntriesCount()
                        //translate if needed
                        if (Locale.getDefault().language.startsWith("ru")
                                || Locale.getDefault().language.startsWith("uk")) {
                            UiTranslateUtils.getTranslatedObject(context, lastNetworkObject)
                        }
                        Timber.d("asFlow: success ApiResponse last object= %s",
                                apiSourcelastObject.data.name)
                        return if (shouldFetch(lastCacheObject, lastNetworkObject, dbObjectsCount)) {
                            //fetch needed = loading, syncing database
                            fetchFromNetwork(pageInfoModel.pages)
                        } else {
                            //if fetch not needed = database is up to date
                            StateResource(Status.Success, Message.DbIsUpToDate)
                        }
                    } else {
                        return manageEmptyOrErrorResponse(apiSourcelastObject)
                    }
                }
            }
            else -> return manageEmptyOrErrorResponse(apiSourceFirstPage)
        }
    }

    /**
     * Called to decide whether an update of the database is needed
     * 1) Fetch if no cached object found
     * 2) Fetch if objects are different (eg language changed, object's updated etc)
     * 3) Fetch if timestamp is older than {Constants.REFRESH_CONSTANT} days
     * 4) Fetch if last network object id > database objects count
     */
    private fun shouldFetch(
            lastCacheObject: ObjectModel?,
            lastNetworkObject: ObjectModel,
            dbObjectsCount: Int): Boolean {
        //fetch if no cached object found
        if (lastCacheObject == null) {
            Timber.d("shouldFetch true: last cache obj is null")
            return true
        }
        //fetch if objects are not equal
        if (!areNetworkAndDbEntriesEqual(lastNetworkObject, lastCacheObject)) {
            Timber.d("shouldFetch true: objects are not equal: \n%s / %s",
                    "lastCacheObject name [${lastCacheObject.name}]",
                    "lastNetworkObject name [${lastNetworkObject.name}]")
            return true
        }
        //fetch if timestamp is older than {Constants.REFRESH_CONSTANT} days
        if (isTimestampObsolete(lastCacheObject)) {
            Timber.d("shouldFetch true: timestamp is older than refreshConstant")
            return true
        }
        //fetch if last network object id > number of database entries
        Timber.d("shouldFetch: lastNetworkObject.getId()= %d, dbCount= %d",
                lastNetworkObject.id,
                dbObjectsCount)
        return if (lastNetworkObject.id > dbObjectsCount) {
            true
        } else {
            Timber.i("shouldFetch: fetch not needed")
            false
        }
    }

    private suspend fun fetchFromNetwork(pageCount: Int): StateResource {
        Timber.i("fetchFromNetwork: getting new data...")
        val resultPageObjects: List<ApiResult<PageModelObject>> = callAllPages(pageCount)
        Timber.d("fetchFromNetwork: pageObjectsList.size= ${resultPageObjects.size}")
        val unsuccessfullResult = {
            resultPage: ApiResult<PageModelObject> ->
            resultPage !is ApiResult.Success<PageModelObject>
        }
        //filter list to successfull results only and save them to db
        val successResultPageObjects = resultPageObjects
                .filterIsInstance<ApiResult.Success<PageModelObject>>()
        /*if list with success results contains data for all pages
                            -> set Resource.success, save results to db*/
        return if (successResultPageObjects.size == pageCount
                && !resultPageObjects.any(unsuccessfullResult)) {
            val successPages = successResultPageObjects.map{it.data}
            saveCallResult(successPages)
            //set status to success -> database is up to date now
            StateResource(
                    Status.Success,
                    Message.DbIsUpToDate
            )
        }
        //or if list has unsuccessfull results set Resource.error
        else {
            Timber.e("fetchFromNetwork: fetch failed")
            val failedResult = resultPageObjects.find(unsuccessfullResult)
            return manageEmptyOrErrorResponse(failedResult)
        }
    }

    /**
     * manages error, failed or empty responses
     * @return StateResource.Error
     */
    private fun <T> manageEmptyOrErrorResponse(
            notSuccessfullResponse: ApiResult<T>?
    ): StateResource =
            when (notSuccessfullResponse) {
                is ApiResult.Failure -> StateResource(
                        status = Status.Error,
                        message = Message.ServerError(notSuccessfullResponse.statusCode?:0)
                )
                ApiResult.NetworkError -> StateResource(
                        status = Status.Error,
                        message = Message.NetworkError
                )
                ApiResult.Empty -> StateResource(
                        status = Status.Error,
                        message = Message.EmptyResponse
                )
                else -> StateResource(
                        status = Status.Error,
                        message = Message.ServerError(0)
                )
            }

    /**
     * compares network and database objects to define if fetch needed
     */
    private fun areNetworkAndDbEntriesEqual(networkObjectModel: ObjectModel,
                                                 cacheObjectModel: ObjectModel): Boolean {
        Timber.d("areNetworkAndDbEntriesEqual: [%b] + IDs: cache [%d], network [%d]",
                networkObjectModel == cacheObjectModel,
                cacheObjectModel.id,
                networkObjectModel.id)
        return networkObjectModel == cacheObjectModel
    }

    /**
     * gets current time in days and compares it to a timestamp from the cache object
     * @return true if timestamp is older than REFRESH_CONSTANT (days)
     */
    private fun isTimestampObsolete(lastCacheObject: ObjectModel): Boolean {
        val currentTimeDays = (System.currentTimeMillis() / 86400000).toInt()
        val lastRefresh = lastCacheObject.timeStamp
        Timber.d("shouldFetch: {currentTime=[%d]} {lastRefresh=[%d]}, last refreshed [%d] days ago",
                currentTimeDays,
                lastRefresh,
                currentTimeDays - lastRefresh)
        return currentTimeDays - lastRefresh >= Constants.DB_REFETCH_PERIOD
    }

    /**
     * call and return last entry from the server
     */
    protected abstract suspend fun callLastApiModel(lastModelId: Int): ApiResult<ObjectModel>

    /**
     * return last entry from the database
     */
    @MainThread
    protected abstract suspend fun getLastEntryFromDb(): ObjectModel?

    /**
     * return number of entries from the database
     */
    @MainThread
    protected abstract suspend fun getDbEntriesCount(): Int

    /**
     * cocurrently call all pages and map the results using flow
     * @return list of all api results with page models
     */
    @MainThread
    protected abstract suspend fun callAllPages(pageCount: Int): List<ApiResult<PageModelObject>>

    /**
     * call server's first page of a given category
     */
    @MainThread
    protected abstract suspend fun createCall(): ApiResult<PageModelObject>

    /**
     * saves the results of the API response into the database
     */
    @WorkerThread
    protected abstract suspend fun saveCallResult(pageModels: List<PageModelObject>)

}