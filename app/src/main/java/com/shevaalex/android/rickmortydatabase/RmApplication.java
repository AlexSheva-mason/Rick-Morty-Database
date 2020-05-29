package com.shevaalex.android.rickmortydatabase;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class RmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupSettings();
        if (BuildConfig.DEBUG) {
            Log.e("onCreate:", "running in DEBUG mode");
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate();
    }

    public void setupSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightOn = sharedPreferences.getBoolean("theme_switch", false);
        int nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        if (nightOn) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
