package com.shevaalex.android.rickmortydatabase.utils.firebase

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLoggerImpl
@Inject
constructor(): FirebaseLogger {

    private val firebaseAnalytics = Firebase.analytics

    override fun logFirebaseEvent(
            eventName: String,
            paramKey: String,
            paramValue: String
    ) {
        firebaseAnalytics.logEvent(eventName) {
            param(paramKey, paramValue)
        }
    }
}