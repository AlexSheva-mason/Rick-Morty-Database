package com.shevaalex.android.rickmortydatabase.models.character

import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiPageInfoModel
import com.shevaalex.android.rickmortydatabase.models.ApiPageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants

class CharacterPageModel (

        apiPageInfoModel: ApiPageInfoModel,

        @SerializedName(ApiConstants.RESULTS_ARRAY)
        val characterModels: List<CharacterModel>

): ApiPageModel(apiPageInfoModel) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharacterPageModel) return false

        if (characterModels != other.characterModels) return false

        return true
    }

    override fun hashCode(): Int {
        return characterModels.hashCode()
    }

    override fun toString(): String {
        return "CharacterPageModel(characterModels=$characterModels)"
    }

}