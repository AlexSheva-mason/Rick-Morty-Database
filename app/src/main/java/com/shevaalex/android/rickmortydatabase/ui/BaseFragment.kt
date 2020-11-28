package com.shevaalex.android.rickmortydatabase.ui

import com.google.firebase.analytics.FirebaseAnalytics
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

abstract class BaseFragment : Fragment() {

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onResume() {
        super.onResume()
        // fragment's class name for firebase logging
        val className = this.javaClass.simpleName
        //log screen view to firebase
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
        }
    }
}