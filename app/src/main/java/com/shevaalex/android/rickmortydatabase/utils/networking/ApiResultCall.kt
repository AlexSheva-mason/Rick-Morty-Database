package com.shevaalex.android.rickmortydatabase.utils.networking

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class ApiResultCall<T>(proxy: Call<T>) : CallDelegate<T, ApiResult<T>>(proxy) {
    override fun enqueueImpl(callback: Callback<ApiResult<T>>) = proxy.enqueue(object: Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            val code = response.code()
            val result = if (code in 200 until 300) {
                val body = response.body()
                val successResult: ApiResult<T> =
                body?.let {
                    ApiResult.Success(it)
                }?: ApiResult.Empty
                successResult
            } else {
                ApiResult.Failure(code)
            }

            callback.onResponse(this@ApiResultCall, Response.success(result))
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            val result = if (t is IOException) {
                ApiResult.NetworkError
            } else {
                Timber.e(t, "ApiResultCall: Failure!")
                ApiResult.Failure(null)
            }

            callback.onResponse(this@ApiResultCall, Response.success(result))
        }
    })

    override fun cloneImpl() = ApiResultCall(proxy.clone())
}