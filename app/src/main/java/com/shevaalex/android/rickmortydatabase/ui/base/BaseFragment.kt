package com.shevaalex.android.rickmortydatabase.ui.base

import com.google.firebase.analytics.FirebaseAnalytics
import androidx.fragment.app.Fragment
import com.shevaalex.android.rickmortydatabase.utils.firebase.FirebaseLogger
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onResume() {
        super.onResume()
        // fragment's class name for firebase logging
        val className = this.javaClass.simpleName
        //log screen view to firebase
        firebaseLogger.logFirebaseEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                FirebaseAnalytics.Param.SCREEN_CLASS,
                className
        )
    }
}