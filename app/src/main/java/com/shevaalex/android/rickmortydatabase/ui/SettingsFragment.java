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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.shevaalex.android.rickmortydatabase.BuildConfig;
import com.shevaalex.android.rickmortydatabase.R;

import static com.shevaalex.android.rickmortydatabase.utils.Constants.KEY_VERSION;
import static com.shevaalex.android.rickmortydatabase.utils.Constants.LIST_THEME_PREFERENCE_KEY;
import static com.shevaalex.android.rickmortydatabase.utils.Constants.SWITCH_THEME_PREFERENCE_KEY;


public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private FirebaseAnalytics mFirebaseAnalytics;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        SwitchPreferenceCompat switchPreference = findPreference(SWITCH_THEME_PREFERENCE_KEY);
        if (switchPreference != null) {
            switchPreference.setOnPreferenceChangeListener(this);
        }
        ListPreference listPreference = findPreference(LIST_THEME_PREFERENCE_KEY);
        if (listPreference != null) {
            listPreference.setOnPreferenceChangeListener(this);
        }
        Preference versionPreference = findPreference(KEY_VERSION);
        if (versionPreference != null) {
            versionPreference.setSummary(getString(R.string.app_name) + getString(R.string.fragment_settings_version) + BuildConfig.VERSION_NAME);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
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
    public void onResume() {
        super.onResume();
        //log screen view to firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, this.getClass().getSimpleName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Bundle params = new Bundle();
        String themeMode;
        switch (preference.getKey()) {
            case SWITCH_THEME_PREFERENCE_KEY:
                boolean nightModeOn = (Boolean) newValue;
                if (nightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    themeMode = "MODE_NIGHT_YES";
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    themeMode = "MODE_NIGHT_NO";
                }
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, themeMode);
                break;
            case LIST_THEME_PREFERENCE_KEY:
                int value = Integer.parseInt((String) newValue);
                AppCompatDelegate.setDefaultNightMode(value);
                switch (value) {
                    case -1:
                        themeMode = "MODE_NIGHT_FOLLOW_SYSTEM";
                        break;
                    case 2:
                        themeMode = "MODE_NIGHT_YES";
                        break;
                    case 1:
                        themeMode = "MODE_NIGHT_NO";
                        break;
                    default:
                        themeMode = "default (unchanged)";
                }
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, themeMode);
        }
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params);
        return true;
    }
}
