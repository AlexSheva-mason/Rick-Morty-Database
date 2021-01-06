package com.shevaalex.android.rickmortydatabase.ui

import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shevaalex.android.rickmortydatabase.BuildConfig
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_REVIEW_SUCCESS_SYNC_UPDATES_NUMBER
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.REVIEW_REQ_SHOW_PERIOD
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.REVIEW_REQ_SUCCESS_SYNC_UPDATES
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class ReviewViewModel
@Inject
constructor(
        private val reviewManager: ReviewManager,
        private val fakeReviewManager: FakeReviewManager,
        private val sharedPrefs: SharedPreferences
) : ViewModel() {

    private var reviewInfo: ReviewInfo? = null

    private var numberOfSuccessDbSyncs =
            sharedPrefs.getInt(KEY_REVIEW_SUCCESS_SYNC_UPDATES_NUMBER, 0)

    private var timestampLastTimeShowedReview =
            sharedPrefs.getInt(KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, 0)

    /**
     * Start requesting the review info (if conditions are met) that will be needed later in advance
     */
    @MainThread
    fun preWarmReview() {
        if (shouldAskForReview() && reviewInfo == null) {
            viewModelScope.launch {
                Timber.w("Requesting reviewInfo...")
                reviewInfo = if (BuildConfig.DEBUG) {
                    fakeReviewManager.requestReview()
                } else {
                    reviewManager.requestReview()
                }
            }
        }
    }

    suspend fun obtainReviewInfo(): ReviewInfo? = withContext(Dispatchers.Main.immediate) {
        reviewInfo?.let {
            reviewInfo.also {
                reviewInfo = null
            }
        }
    }

    /**
     * increments the number of successful db sync events numberOfSuccessDbSyncs
     */
    fun notifyDbSyncSuccessful() {
        Timber.i("saving new numberOfSuccessDbSyncs to sharedPrefs: old=%s / new =%s",
                numberOfSuccessDbSyncs,
                numberOfSuccessDbSyncs + 1
        )
        numberOfSuccessDbSyncs++
        with(sharedPrefs.edit()) {
            putInt(KEY_REVIEW_SUCCESS_SYNC_UPDATES_NUMBER, numberOfSuccessDbSyncs)
            apply()
        }
    }

    /**
     * sets the timestamp of when review was showed
     */
    fun notifyReviewFlowLaunched() {
        resetNumberOfSuccessDbSyncs()
        Timber.i("saving new timestampLastTimeShowedReview to sharedPrefs: old=%s / new=%s",
                timestampLastTimeShowedReview,
                (System.currentTimeMillis() / 86400000).toInt()
        )
        //sets timestamp to current time in days
        timestampLastTimeShowedReview = (System.currentTimeMillis() / 86400000).toInt()
        with (sharedPrefs.edit()) {
            putInt(KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, timestampLastTimeShowedReview)
            apply()
        }
    }

    /**
     * based on values of numberOfSuccessDbSyncs and timestampLastTimeShowedReview
     * defines if review info should be fetched
     * @return true if both required number of success db sync events were registered
     *                                  and required minimum time between review dialogs elapsed
     */
    private fun shouldAskForReview(): Boolean {
        val currentTimeDays = (System.currentTimeMillis() / 86400000).toInt()
        val shouldAsk = numberOfSuccessDbSyncs >= REVIEW_REQ_SUCCESS_SYNC_UPDATES
                && currentTimeDays - timestampLastTimeShowedReview > REVIEW_REQ_SHOW_PERIOD
        if (shouldAsk) {
            Timber.i("[shouldAskForReview()] numberOfSuccessDbSyncs=%s / REVIEW_REQ_SUCCESS_SYNC_UPDATES=%s",
                    numberOfSuccessDbSyncs,
                    REVIEW_REQ_SUCCESS_SYNC_UPDATES
            )
            Timber.i(
                    "[shouldAskForReview()] currentTimeDays=%s / timestampLastTimeShowedReview=%s / REVIEW_REQ_SHOW_PERIOD=%s",
                    currentTimeDays,
                    timestampLastTimeShowedReview,
                    REVIEW_REQ_SHOW_PERIOD
            )
            Timber.i(
                    "[shouldAskForReview()] timestampDiff=%s / shouldAsk=%s",
                    currentTimeDays - timestampLastTimeShowedReview,
                    shouldAsk
            )
        }
        return shouldAsk
    }

    /**
     * called when review flow was launched to reset the count of numberOfSuccessDbSyncs
     */
    private fun resetNumberOfSuccessDbSyncs() {
        numberOfSuccessDbSyncs = 0
        with(sharedPrefs.edit()) {
            putInt(KEY_REVIEW_SUCCESS_SYNC_UPDATES_NUMBER, numberOfSuccessDbSyncs)
            Timber.i("resetting the numberOfSuccessDbSyncs to sharedPrefs: %s", numberOfSuccessDbSyncs)
            apply()
        }
    }

}