package com.shevaalex.android.rickmortydatabase.models.episode

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.networking.EPISODE_CHARACTERS
import com.shevaalex.android.rickmortydatabase.utils.networking.EPISODE_CODE
import kotlinx.parcelize.Parcelize

@Entity
@Keep
@Parcelize
data class EpisodeModel(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override var name: String,

        override var timeStamp: Int,

        var airDate: String,

        @SerializedName(EPISODE_CODE)
        val code: String,

        override val imageUrl: String?,

        val description: String?,

        @SerializedName(EPISODE_CHARACTERS)
        val charactersList: Array<String>

): ApiObjectModel, Parcelable {

    val characterIds: List<Int>
        get() {
            return charactersList.mapNotNull {characterUrl ->
                characterUrl.dropWhile {char ->
                    !char.isDigit()
                }.toIntOrNull()
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EpisodeModel) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (airDate != other.airDate) return false
        if (code != other.code) return false
        if (imageUrl != other.imageUrl) return false
        if (description != other.description) return false
        if (!charactersList.contentEquals(other.charactersList)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + airDate.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + charactersList.contentHashCode()
        return result
    }

}