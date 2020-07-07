package com.shevaalex.android.rickmortydatabase.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import android.view.View;
import android.widget.TextView;

import com.shevaalex.android.rickmortydatabase.BuildConfig;
import com.shevaalex.android.rickmortydatabase.R;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private final static String KEY_THEME_SWITCH = "theme_switch";
    private final static String KEY_THEME_LIST = "theme_list";
    private final static String KEY_VERSION = "version";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        SwitchPreferenceCompat switchPreference = findPreference(KEY_THEME_SWITCH);
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
        ListPreference listPreference = findPreference(KEY_THEME_LIST);
        if (listPreference != null) {
            listPreference.setOnPreferenceChangeListener(this);
        }
        Preference versionPreference = findPreference(KEY_VERSION);
        if (versionPreference != null) {
            versionPreference.setSummary(getString(R.string.app_name) + getString(R.string.fragment_settings_version) + BuildConfig.VERSION_NAME);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.charactersListFragment, R.id.locationsListFragment, R.id.episodesListFragment).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_fragment_simple);
        if (toolbar != null) {
            NavigationUI.setupWithNavController(
                    toolbar, navController, appBarConfiguration);
            TextView titleTextView = view.findViewById(R.id.toolbar_title);
            titleTextView.setText(toolbar.getTitle());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case KEY_THEME_SWITCH:
                boolean nightModeOn = (Boolean) newValue;
                if (nightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                break;
            case KEY_THEME_LIST:
                int value = Integer.parseInt((String) newValue);
                AppCompatDelegate.setDefaultNightMode(value);
        }
        return true;
    }
}
