package com.shevaalex.android.rickmortydatabase

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.shevaalex.android.rickmortydatabase.di.AppComponent
import com.shevaalex.android.rickmortydatabase.di.DaggerAppComponent
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.LIST_THEME_PREFERENCE_KEY
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.SWITCH_THEME_PREFERENCE_KEY
import com.shevaalex.android.rickmortydatabase.utils.CustomDebugTree
import timber.log.Timber


class RmApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }


    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupTheme()
        //set Strict mode in debug builds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setupStrictMode()
        } else {
            setupStrictModeLegacy()
        }
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(CustomDebugTree())
        }
    }

    //get saved preferences or set default theme if none stored
    private fun setupTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val savedListPrefTheme =
                sharedPreferences.getString(LIST_THEME_PREFERENCE_KEY, "99")?.toInt()
        val savedSwitchThemeNightOn =
                sharedPreferences.getBoolean(SWITCH_THEME_PREFERENCE_KEY, false)
        if (Build.VERSION.SDK_INT >= 29) {
            if (savedListPrefTheme == 99) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            } else if (savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM || savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_NO || savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(savedListPrefTheme)
            }
        } else {
            when (savedSwitchThemeNightOn) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build())
        }
    }

    private fun setupStrictModeLegacy() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build())
            StrictMode.setVmPolicy(VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build())
        }
    }

}