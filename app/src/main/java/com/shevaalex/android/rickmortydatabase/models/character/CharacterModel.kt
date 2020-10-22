package com.shevaalex.android.rickmortydatabase.models.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants.ApiCallCharacterKeys

@Entity
data class CharacterModel(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override var name: String,

        override var timeStamp: Int,

        var status: String,

        var species: String,

        var gender: String,

        @SerializedName(ApiCallCharacterKeys.CHARACTER_ORIGIN_LOCATION)
        val originLocation: LinkedLocationModel,

        @SerializedName(ApiCallCharacterKeys.CHARACTER_LAST_LOCATION)
        val lastLocation: LinkedLocationModel,

        @SerializedName(ApiCallCharacterKeys.CHARACTER_IMAGE_URL)
        val imageUrl: String,

        @SerializedName(ApiCallCharacterKeys.CHARACTER_EPISODE_LIST)
        val episodeList: Array<String>,

): ApiObjectModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharacterModel) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (status != other.status) return false
        if (species != other.species) return false
        if (gender != other.gender) return false
        if (originLocation != other.originLocation) return false
        if (lastLocation != other.lastLocation) return false
        if (imageUrl != other.imageUrl) return false
        if (!episodeList.contentEquals(other.episodeList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + species.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + originLocation.hashCode()
        result = 31 * result + lastLocation.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + episodeList.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "CharacterModel(id=$id," +
                " name='$name'," +
                " status='$status'," +
                " species='$species'," +
                " gender='$gender'," +
                " originLocation=$originLocation," +
                " lastLocation=$lastLocation)"
    }

}