package com.shevaalex.android.rickmortydatabase.utils.networking

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val statusCode: Int?) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
    object Empty : ApiResult<Nothing>()
}
