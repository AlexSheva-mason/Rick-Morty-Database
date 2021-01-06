package com.shevaalex.android.rickmortydatabase.models.episode

import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiPageInfoModel
import com.shevaalex.android.rickmortydatabase.models.ApiPageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants

class EpisodePageModel(

        apiPageInfoModel: ApiPageInfoModel,

        @SerializedName(ApiConstants.RESULTS_ARRAY)
        val episodeModels: List<EpisodeModel>

): ApiPageModel(apiPageInfoModel) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EpisodePageModel) return false
        if (!super.equals(other)) return false

        if (episodeModels != other.episodeModels) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + episodeModels.hashCode()
        return result
    }

    override fun toString(): String {
        return "EpisodePageModel(episodeModels=$episodeModels)"
    }

}