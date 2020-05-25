package com.shevaalex.android.rickmortydatabase;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class RmApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO sort this out
        int nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        //int nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        /*int nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        int nightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else {
            int nightMode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
        }*/
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
