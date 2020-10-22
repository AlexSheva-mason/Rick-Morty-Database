package com.shevaalex.android.rickmortydatabase.models.location

import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiPageInfoModel
import com.shevaalex.android.rickmortydatabase.models.ApiPageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants

class LocationPageModel(

        apiPageInfoModel: ApiPageInfoModel,

        @SerializedName(ApiConstants.RESULTS_ARRAY)
        val locationModels: List<LocationModel>

): ApiPageModel(apiPageInfoModel) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationPageModel) return false
        if (!super.equals(other)) return false

        if (locationModels != other.locationModels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + locationModels.hashCode()
        return result
    }

    override fun toString(): String {
        return "LocationPageModel(locationModels=$locationModels)"
    }

}