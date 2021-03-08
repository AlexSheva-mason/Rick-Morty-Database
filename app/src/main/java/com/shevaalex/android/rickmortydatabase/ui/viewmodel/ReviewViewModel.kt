package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_APP_LAUNCH_NUMBER
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.REVIEW_REQ_SHOW_PERIOD
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.REVIEW_REQUIRED_NUMBER_APP_LAUNCHED
import com.shevaalex.android.rickmortydatabase.utils.firebase.FirebaseLogger
import com.shevaalex.android.rickmortydatabase.utils.currentTimeDays
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ReviewViewModel
@Inject
constructor(
        private val reviewManager: ReviewManager,
        private val sharedPrefs: SharedPreferences,
        private val firebaseLogger: FirebaseLogger
) : ViewModel() {

    private var reviewInfo: Deferred<ReviewInfo>? = null

    private var appLaunchedNumber =
            sharedPrefs.getInt(KEY_APP_LAUNCH_NUMBER, 0)

    private var timestampLastTimeShowedReview =
            sharedPrefs.getInt(KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, 0)

    init {
        incrementAppLaunched()
    }

    /**
     * Start requesting the review info (if conditions are met) that will be needed later in advance
     */
    @MainThread
    fun preWarmReview() {
        if (shouldAskForReview() && reviewInfo == null) {
            Timber.w("Requesting reviewInfo...")
            reviewInfo = viewModelScope.async {
                reviewManager.requestReview()
            }
        }
    }

    suspend fun obtainReviewInfo(): ReviewInfo? = withContext(Dispatchers.Main.immediate) {
        if (reviewInfo?.isCompleted == true && reviewInfo?.isCancelled == false) {
            reviewInfo?.getCompleted().also {
                reviewInfo = null
            }
        } else null
    }

    /**
     * sets the timestamp of when review was showed
     */
    fun notifyReviewFlowLaunched() {
        logReviewToFirebase()
        resetNumberAppLaunched()
        saveNewTimestampLastTimeShowedReview()
    }

    /**
     * based on values of appLaunchedNumber and timestampLastTimeShowedReview
     * defines if review info should be fetched
     * @return true if both required number of app launched events were registered
     *                                  and required minimum time between review dialogs elapsed
     */
    private fun shouldAskForReview(): Boolean {
        val currentTimeDays = currentTimeDays()
        val shouldAsk = appLaunchedNumber >= REVIEW_REQUIRED_NUMBER_APP_LAUNCHED
                && currentTimeDays - timestampLastTimeShowedReview > REVIEW_REQ_SHOW_PERIOD
        if (shouldAsk) {
            Timber.d("[shouldAskForReview()] numberOfSuccessDbSyncs=%s / REVIEW_REQ_SUCCESS_SYNC_UPDATES=%s",
                    appLaunchedNumber,
                    REVIEW_REQUIRED_NUMBER_APP_LAUNCHED
            )
            Timber.d(
                    "[shouldAskForReview()] currentTimeDays=%s / timestampLastTimeShowedReview=%s / REVIEW_REQ_SHOW_PERIOD=%s",
                    currentTimeDays,
                    timestampLastTimeShowedReview,
                    REVIEW_REQ_SHOW_PERIOD
            )
            Timber.d(
                    "[shouldAskForReview()] timestampDiff=%s / shouldAsk=%s",
                    currentTimeDays - timestampLastTimeShowedReview,
                    shouldAsk
            )
        }
        return shouldAsk
    }

    /**
     * called when review flow was launched to reset the app launched number
     */
    private fun resetNumberAppLaunched() {
        appLaunchedNumber = 0
        with(sharedPrefs.edit()) {
            putInt(KEY_APP_LAUNCH_NUMBER, appLaunchedNumber)
            apply()
        }
    }

    private fun saveNewTimestampLastTimeShowedReview() {
        Timber.d("saving new timestampLastTimeShowedReview to sharedPrefs: old=%s / new=%s",
                timestampLastTimeShowedReview,
                currentTimeDays()
        )
        //sets timestamp to current time in days
        timestampLastTimeShowedReview = currentTimeDays()
        with(sharedPrefs.edit()) {
            putInt(KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, timestampLastTimeShowedReview)
            apply()
        }
    }

    private fun logReviewToFirebase() {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        firebaseLogger.logFirebaseEvent(
                eventName = "google_review_flow_launched",
                paramKey = FirebaseAnalytics.Param.START_DATE,
                paramValue = dateFormatter.format(Calendar.getInstance().time)
        )
    }

    /**
     * increments the app launched number
     */
    private fun incrementAppLaunched() {
        Timber.d("saving new appLaunchNumber to sharedPrefs: old=%s / new =%s",
                appLaunchedNumber,
                appLaunchedNumber + 1
        )
        appLaunchedNumber++
        with(sharedPrefs.edit()) {
            putInt(KEY_APP_LAUNCH_NUMBER, appLaunchedNumber)
            apply()
        }
    }

}