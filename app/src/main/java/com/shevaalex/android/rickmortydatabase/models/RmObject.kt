package com.shevaalex.android.rickmortydatabase.models

import android.os.Parcelable
import androidx.annotation.Keep

@Keep
interface RmObject: Parcelable {

    val id: Int

    val name: String

    val imageUrl: String?

}