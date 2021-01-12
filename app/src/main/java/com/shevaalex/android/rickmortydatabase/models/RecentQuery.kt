package com.shevaalex.android.rickmortydatabase.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class RecentQuery(

        @PrimaryKey(autoGenerate = true)
        val id: Int,

        val name: String,

        val type: String

) {
        enum class Type(val type: String) {
                CHARACTER("character"),
                LOCATION("location"),
                EPISODE("episode")
        }

}