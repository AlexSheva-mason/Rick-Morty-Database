package com.shevaalex.android.rickmortydatabase.utils.firebase

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeFirebaseLogger
@Inject
constructor() : FirebaseLogger {

    var loggedEvent: String? = null

    override fun logFirebaseEvent(eventName: String, paramKey: String, paramValue: String) {
        loggedEvent = eventName
        Timber.i("Firebase unit test log: $eventName")
    }

    override fun logException(exception: Exception) {}

}
