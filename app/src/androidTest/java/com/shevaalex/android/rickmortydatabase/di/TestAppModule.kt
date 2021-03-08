package com.shevaalex.android.rickmortydatabase.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shevaalex.android.rickmortydatabase.TestRmApplication
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.SHARED_PREFS_FILE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [TestImplBindingModule::class])
object TestAppModule {

    @Singleton
    @Provides
    fun provideReviewManager(app: TestRmApplication): ReviewManager {
        return FakeReviewManager(app)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(app: TestRmApplication): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideApplication (app: TestRmApplication): Application {
        return app
    }

}