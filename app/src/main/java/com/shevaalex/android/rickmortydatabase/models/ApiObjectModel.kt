package com.shevaalex.android.rickmortydatabase.models

import android.os.Parcelable

interface ApiObjectModel: Parcelable {

    val id: Int

    var name: String

    var timeStamp: Int

}