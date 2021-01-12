package com.shevaalex.android.rickmortydatabase.models

import android.os.Parcelable
import androidx.annotation.Keep

@Keep
interface ApiObjectModel: Parcelable {

    val id: Int

    var name: String

    val imageUrl: String?

}