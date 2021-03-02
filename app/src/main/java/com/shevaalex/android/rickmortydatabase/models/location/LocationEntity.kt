package com.shevaalex.android.rickmortydatabase.models.location

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.shevaalex.android.rickmortydatabase.models.RmObject
import com.shevaalex.android.rickmortydatabase.utils.networking.LOCATION_RESIDENTS
import kotlinx.parcelize.Parcelize

@Entity
@Keep
@Parcelize
data class LocationEntity(

        @PrimaryKey
        override val id: Int,

        @ColumnInfo(collate = ColumnInfo.LOCALIZED)
        override val name: String,

        val type: String,

        val dimension: String,

        override val imageUrl: String?,

        @SerializedName(LOCATION_RESIDENTS)
        val characters: List<String>?

): RmObject, Parcelable {

    val characterIds: List<Int>
        get() {
            return characters?.mapNotNull {characterUrl ->
                characterUrl.dropWhile {char ->
                    !char.isDigit()
                }.toIntOrNull()
            }?: listOf(0)
        }

}