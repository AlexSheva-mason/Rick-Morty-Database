package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.play.core.review.ReviewManager
import com.google.common.truth.Truth.assertThat
import com.google.firebase.analytics.FirebaseAnalytics
import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.di.TestAppComponent
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.currentTimeDays
import com.shevaalex.android.rickmortydatabase.utils.firebase.FirebaseLogger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
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

    private val application: TestRmApplication =
        ApplicationProvider.getApplicationContext() as TestRmApplication

    private lateinit var viewModel: ReviewViewModel

    @Inject
    lateinit var reviewManager: ReviewManager

    @MockK
    lateinit var sharedPrefs: SharedPreferences

    @MockK
    lateinit var mockEditor: SharedPreferences.Editor

    @MockK
    lateinit var firebaseLogger: FirebaseLogger

    init {
        injectTest()
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every {
            sharedPrefs.edit()
        } returns mockEditor
        every {
            mockEditor.putInt(any(), any())
        } returns mockEditor
        every {
            mockEditor.apply()
        } returns Unit
        every {
            sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        } returns 0
        every {
            sharedPrefs.getInt(Constants.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, 0)
        } returns 0
        every {
            firebaseLogger.logFirebaseEvent(any(), any(), any())
        } returns Unit
    }

    private fun injectTest() {
        (application.appComponent as TestAppComponent)
            .inject(this)
    }

    @Test
    fun fakeReviewManagerReturnsReviewInfo() {
        assertThat(reviewManager.requestReviewFlow()).isNotNull()
    }

    /**
     * tests the incrementAppLaunched() function
     */
    @Test
    fun instantiatingViewModelIncrementsNumberAppLaunched() = runBlocking {
        val initialValue = Random.nextInt(3, 99)
        every {
            sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        } returns initialValue
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        verifySequence {
            mockEditor.putInt(Constants.KEY_APP_LAUNCH_NUMBER, initialValue + 1)
            mockEditor.apply()
        }
    }

    /**
     * shouldAskForReview() = false
     * reviewInfo == null
     */
    @Test
    fun preWarmReviewWithShouldAskForReviewFalseShouldNotStartTheReviewFlow() = runBlocking {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModelRequestReview()
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
        every {
            sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        } returns Constants.REVIEW_REQUIRED_NUMBER_APP_LAUNCHED
        //instantiate the vm
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModelRequestReview()
        //request review info
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
        every {
            sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        } returns Constants.REVIEW_REQUIRED_NUMBER_APP_LAUNCHED
        //instantiate the vm
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModelRequestReview()
        //get review info
        viewModel.obtainReviewInfo()
        val result = viewModel.obtainReviewInfo()
        assertThat(result).isNull()
    }

    @Test
    fun notifyReviewFlowLaunchedShouldLogToFirebase() {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        verifySequence {
            firebaseLogger.logFirebaseEvent(
                eventName = "google_review_flow_launched",
                paramKey = FirebaseAnalytics.Param.START_DATE,
                paramValue = any()
            )
        }
    }

    @Test
    fun notifyReviewFlowLaunchedShouldResetNumberAppLaunched() {
        //set appLaunchedNumber to !=0
        every {
            sharedPrefs.getInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        } returns 2
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        verify(exactly = 1) {
            mockEditor.putInt(Constants.KEY_APP_LAUNCH_NUMBER, 0)
        }
    }

    @Test
    fun notifyReviewFlowLaunchedShouldSetNewTimestamp() {
        viewModel = ReviewViewModel(reviewManager, sharedPrefs, firebaseLogger)
        viewModel.notifyReviewFlowLaunched()
        verify(exactly = 1) {
            mockEditor.putInt(Constants.KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP, currentTimeDays())
        }
    }

    private suspend fun viewModelRequestReview() {
        viewModel.preWarmReview()
        delay(100)
    }

}
