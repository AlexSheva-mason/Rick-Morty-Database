package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.play.core.review.ReviewManager
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.currentTimeDays
import com.shevaalex.android.rickmortydatabase.utils.firebase.FakeFirebaseLogger
import com.shevaalex.android.rickmortydatabase.utils.firebase.FirebaseLogger

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.random.Random

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReviewViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application: TestRmApplication = ApplicationProvider.getApplicationContext() as TestRmApplication

    private lateinit var viewModel: ReviewViewModel

    @Inject
    lateinit var reviewManager: ReviewManager

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    init {
        injectTest()
    }

    private fun injectTest() {
        (application.appComponent as TestAppComponent)
                .inject(this)
    }

    @After
    fun tearDown() {
        //reset shared preferences
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
            apply()
        }
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, 0)
            apply()
        }
        //reset the FirebaseLogger
        (firebaseLogger as FakeFirebaseLogger).loggedEvent = null
    }

    /**
     * tests the incrementAppLaunched() function
     */
    @Test
    fun instantiatingViewModelIncrementsNumberAppLaunched() = runBlocking {
        val initialValue = Random.nextInt(3, 99)
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_APP_LAUNCH_NUMBER, initialValue)
            apply()
        }
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        val resultAppLaunchedNumber = sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        assertThat(resultAppLaunchedNumber).isEqualTo(initialValue+1)
    }

    /**
     * shouldAskForReview() = false
     * reviewInfo == null
     */
    @Test
    fun preWarmReviewWithShouldAskForReviewFalseShouldNotStartTheReviewFlow() = runBlocking {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.preWarmReview()
        val result = viewModel.obtainReviewInfo()
        assertThat(result).isNull()
    }

    /**
     * shouldAskForReview() = true
     * reviewInfo == null
     */
    @Test
    fun preWarmReviewWithReviewInfoNullShouldStartTheReviewFlow() = runBlocking {
        //make shouldAskForReview return true
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_APP_LAUNCH_NUMBER, Constants.REVIEW_REQUIRED_NUMBER_APP_LAUNCHED)
            apply()
        }
        //instantiate the vm
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        //request review info
        viewModel.preWarmReview()
        val result = viewModel.obtainReviewInfo()
        assertThat(result).isNotNull()
    }

    /**
     * shouldAskForReview() = true
     * reviewInfo == null
     */
    @Test
    fun obtainReviewInfoResetsReviewInfoToNull() = runBlocking {
        //make shouldAskForReview return true
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_APP_LAUNCH_NUMBER, Constants.REVIEW_REQUIRED_NUMBER_APP_LAUNCHED)
            apply()
        }
        //instantiate the vm
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        //request review info
        viewModel.preWarmReview()
        viewModel.obtainReviewInfo()
        val result = viewModel.obtainReviewInfo()
        assertThat(result).isNull()
    }

    @Test
    fun notifyReviewFlowLaunchedShouldLogToFirebase() {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        val loggedString = (firebaseLogger as FakeFirebaseLogger).loggedEvent
        assertThat(loggedString).isEqualTo("google_review_flow_launched")
    }

    @Test
    fun notifyReviewFlowLaunchedShouldResetNumberAppLaunched() {
        //set appLaunchedNumber to !=0
        with(sharedPrefs.edit()) {
            putInt(Constants.KEY_APP_LAUNCH_NUMBER, 2)
            apply()
        }
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        val appLaunchedNumber = sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        assertThat(appLaunchedNumber).isEqualTo(0)
    }

    @Test
    fun notifyReviewFlowLaunchedShouldSetNewTimestamp() {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        val timestampLastTimeShowedReview =
                sharedPrefs.getInt(Constants.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, 0)
        assertThat(timestampLastTimeShowedReview).isEqualTo(currentTimeDays())
    }

}