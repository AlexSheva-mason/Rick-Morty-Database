package com.shevaalex.android.rickmortydatabase.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.SHARED_PREFS_FILE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Singleton
    @Provides
    fun provideReviewManager(app: Application): ReviewManager {
        return ReviewManagerFactory.create(app)
    }

    @Singleton
    @Provides
    fun provideFakeReviewManager(app: Application): FakeReviewManager {
        return FakeReviewManager(app)
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

}