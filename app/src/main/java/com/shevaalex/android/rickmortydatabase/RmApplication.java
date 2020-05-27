package com.shevaalex.android.rickmortydatabase;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class RmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupSettings();
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
