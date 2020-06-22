package com.shevaalex.android.rickmortydatabase;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class RmApplication extends Application {
    private static final String LIST_PREFERENCE_KEY = "theme_list";
    public static String defSystemLanguage;

    @Override
    public void onCreate() {
        super.onCreate();
        setupTheme();
        //set Strict mode in debug builds
        if (BuildConfig.DEBUG) {
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
        defSystemLanguage = Locale.getDefault().getLanguage();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        defSystemLanguage = newConfig.locale.getLanguage();
    }

    //get saved preferences or set default theme if none stored
    public void setupTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int savedListPrefTheme = Integer.parseInt(sharedPreferences.getString(LIST_PREFERENCE_KEY, "99"));
        boolean savedSwitchThemeNightOn = sharedPreferences.getBoolean("theme_switch", false);
        if (Build.VERSION.SDK_INT >= 29) {
            if (savedListPrefTheme == 99) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    || savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_NO
                    || savedListPrefTheme == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(savedListPrefTheme);
            }
        } else {
            int nightMode = AppCompatDelegate.MODE_NIGHT_NO;
            if (savedSwitchThemeNightOn) {
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
            }
            AppCompatDelegate.setDefaultNightMode(nightMode);
        }
    }
}
