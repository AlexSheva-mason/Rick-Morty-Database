package com.shevaalex.android.rickmortydatabase.models.episode

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.RmObject
import com.shevaalex.android.rickmortydatabase.utils.networking.EPISODE_CHARACTERS
import com.shevaalex.android.rickmortydatabase.utils.networking.EPISODE_CODE
import kotlinx.parcelize.Parcelize

@Entity
@Keep
@Parcelize
data class EpisodeEntity(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override val name: String,

        val airDate: String,

        @SerializedName(EPISODE_CODE)
        val code: String,

        override val imageUrl: String?,

        val description: String?,

        @SerializedName(EPISODE_CHARACTERS)
        val charactersList: List<String>

): RmObject, Parcelable {

    val characterIds: List<Int>
        get() {
            return charactersList.mapNotNull {characterUrl ->
                characterUrl.dropWhile {char ->
                    !char.isDigit()
                }.toIntOrNull()
            }
        }

}