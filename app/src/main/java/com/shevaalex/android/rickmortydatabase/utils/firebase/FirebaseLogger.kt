package com.shevaalex.android.rickmortydatabase.utils.firebase

interface FirebaseLogger {

    fun logFirebaseEvent(
        eventName: String,
        paramKey: String,
        paramValue: String
    )

    fun logException(
        exception: Exception
    )

}
