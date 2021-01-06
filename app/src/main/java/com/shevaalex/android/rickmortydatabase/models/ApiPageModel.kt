package com.shevaalex.android.rickmortydatabase.models

import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants

abstract class ApiPageModel(
        @SerializedName(ApiConstants.INFO)
        val apiPageInfoModel: ApiPageInfoModel
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApiPageModel) return false

        if (apiPageInfoModel != other.apiPageInfoModel) return false

        return true
    }

    override fun hashCode(): Int {
        return apiPageInfoModel.hashCode()
    }

    override fun toString(): String {
        return "ApiPageModel(apiPageInfoModel=$apiPageInfoModel)"
    }
}