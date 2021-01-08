package com.shevaalex.android.rickmortydatabase.models.character

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LinkedLocationModel(
        var name: String,
        val url: String,
): Parcelable {

    val id: Int get() {
        return url.substringAfterLast("/").toIntOrNull()?:-1
    }

}