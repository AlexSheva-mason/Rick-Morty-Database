package com.shevaalex.android.rickmortydatabase.models.character

import androidx.room.Entity
import androidx.room.PrimaryKey


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