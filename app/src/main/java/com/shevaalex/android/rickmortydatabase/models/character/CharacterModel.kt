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
        override val name: String,

        val status: String,

        val species: String,

        val gender: String,

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

}