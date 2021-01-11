package com.shevaalex.android.rickmortydatabase.models.character

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel
import kotlinx.parcelize.Parcelize

@Entity
@Keep
@Parcelize
data class CharacterModel(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override var name: String,

        var status: String,

        var species: String,

        var gender: String,

        val originLocation: LinkedLocationModel,

        val lastLocation: LinkedLocationModel,

        override val imageUrl: String,

        val episodeList: List<String>

): ApiObjectModel, Parcelable {

    val episodeIds: List<Int>
        get() {
            return episodeList.mapNotNull {episodeUrl ->
                episodeUrl.dropWhile {char ->
                    !char.isDigit()
                }.toIntOrNull()
            }
        }

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
        if (episodeList != other.episodeList) return false
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
        result = 31 * result + episodeList.hashCode()
        return result
    }

}