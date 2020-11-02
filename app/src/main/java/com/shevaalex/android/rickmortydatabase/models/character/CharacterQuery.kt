package com.shevaalex.android.rickmortydatabase.models.character

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CharacterQuery(

        @PrimaryKey(autoGenerate = true)
        val id: Int,

        val name: String

)