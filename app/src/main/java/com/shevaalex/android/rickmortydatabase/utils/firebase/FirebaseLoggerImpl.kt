package com.shevaalex.android.rickmortydatabase.utils.firebase

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseLoggerImpl
@Inject
constructor() : FirebaseLogger {

    private val firebaseAnalytics = Firebase.analytics
    private val firebaseCrashlytics = FirebaseCrashlytics.getInstance()

    override fun logFirebaseEvent(
        eventName: String,
        paramKey: String,
        paramValue: String
    ) {
        firebaseAnalytics.logEvent(eventName) {
            param(paramKey, paramValue)
        }
    }

    override fun logException(exception: Exception) {
        firebaseCrashlytics.recordException(exception)
    }

}
