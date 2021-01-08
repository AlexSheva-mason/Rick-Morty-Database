package com.shevaalex.android.rickmortydatabase.models.location

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import com.shevaalex.android.rickmortydatabase.utils.networking.LOCATION_RESIDENTS
import kotlinx.parcelize.Parcelize

@Entity
@Keep
@Parcelize
data class LocationModel(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override var name: String,

        override var timeStamp: Int,

        var type: String,

        var dimension: String,

        override val imageUrl: String?,

        @SerializedName(LOCATION_RESIDENTS)
        val characters: List<String>

): ApiObjectModel, Parcelable {

    val characterIds: List<Int>
        get() {
            return characters.mapNotNull {characterUrl ->
                characterUrl.dropWhile {char ->
                    !char.isDigit()
                }.toIntOrNull()
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationModel) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (dimension != other.dimension) return false
        if (imageUrl != other.imageUrl) return false
        if (characters != other.characters) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + dimension.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + characters.hashCode()
        return result
    }

}