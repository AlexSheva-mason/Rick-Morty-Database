package com.shevaalex.android.rickmortydatabase.repository.init

import android.content.SharedPreferences
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.currentTimeHours
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber

/**
 * save the timestamp with the time when objects were refetched and saved to local db
 */
fun saveFetchedTimestampToSharedPrefs(sharedPref: SharedPreferences, key: String) {
    with(sharedPref.edit()) {
        val currentTimeHrs = currentTimeHours().toInt()
        Timber.d("saving $key timestamp: %s", currentTimeHrs)
        putInt(key, currentTimeHrs)
        apply()
    }
}

/**
 * @return true if currentTimeHrs - lastSynced is more than OBJECT_REFETCH_PERIOD (hrs)
 */
fun isRefetchNeeded(sharedPref: SharedPreferences, key: String): Boolean {
    val lastSynced = sharedPref.getInt(key, 0)
    val currentTimeHrs = currentTimeHours().toInt()
    Timber.d(
            "isRefetchNeeded, lastSync: %s, currentTimeHrs: %s, diff: %s, isRefetchNeeded:%s",
            lastSynced,
            currentTimeHrs,
            currentTimeHrs - lastSynced,
            currentTimeHrs - lastSynced >= Constants.OBJECT_REFETCH_PERIOD
    )
    return currentTimeHrs - lastSynced >= Constants.OBJECT_REFETCH_PERIOD
}

/**
 * returns a Status.Error according to an error
 */
fun <T> manageEmptyOrErrorResponse(
        notSuccessfullResponse: ApiResult<T>?
): StateResource =
        when (notSuccessfullResponse) {
            is ApiResult.Failure -> StateResource(
                    status = Status.Error,
                    message = Message.ServerError(notSuccessfullResponse.statusCode ?: 0)
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