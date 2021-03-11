package com.shevaalex.android.rickmortydatabase.utils.firebase

class FakeTestFirebaseLogger : FirebaseLogger {

    var loggedEvent: String? = null

    override fun logFirebaseEvent(eventName: String, paramKey: String, paramValue: String) {
        loggedEvent = eventName
    }

}