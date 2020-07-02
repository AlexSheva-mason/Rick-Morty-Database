package com.shevaalex.android.rickmortydatabase.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.preference.PreferenceManager;

import java.util.Locale;

/** Manages setting of the app's locale. */
public class LocaleUtils {
    private static final String LIST_LANGUAGE_PREFERENCE_KEY = "language_list";
    private static final String LOCALE_KEY_SYSTEM = "system";

    public static Context onAttach(Context context) {
        String locale = getPersistedLocale(context);
        return setLocale(context, locale);
    }

    public static String getAppLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String savedLocale = sharedPreferences.getString(LIST_LANGUAGE_PREFERENCE_KEY, "");
        Locale locale;
        locale = new Locale(savedLocale);
        if (savedLocale.isEmpty() || savedLocale.equals(LOCALE_KEY_SYSTEM)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                locale = Resources.getSystem().getConfiguration().locale;
            }
        }
        return locale.toString();
    }

    public static String getPersistedLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(LIST_LANGUAGE_PREFERENCE_KEY, "");
    }

    public static Context setLocale(Context context, String localeSpec) {
        Locale locale;
        if (localeSpec.equals(LOCALE_KEY_SYSTEM)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = Resources.getSystem().getConfiguration().getLocales().get(0);
            } else {
                locale = Resources.getSystem().getConfiguration().locale;
            }
        } else {
            locale = new Locale(localeSpec);
        }
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return updateResourcesLegacy(context, locale);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        LocaleList localeList = new LocaleList(locale);
        configuration.setLocales(localeList);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}