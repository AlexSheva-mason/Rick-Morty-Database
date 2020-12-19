package com.shevaalex.android.rickmortydatabase.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.shevaalex.android.rickmortydatabase.BuildConfig
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.utils.Constants

class SettingsFragment: PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val switchPreference =
                findPreference<SwitchPreferenceCompat>(Constants.SWITCH_THEME_PREFERENCE_KEY)
        val listPreference = findPreference<ListPreference>(Constants.LIST_THEME_PREFERENCE_KEY)
        val versionPreference = findPreference<Preference>(Constants.KEY_VERSION)
        switchPreference?.let {
            it.onPreferenceChangeListener = this
        }
        listPreference?.let {
            it.onPreferenceChangeListener = this
        }
        versionPreference?.let {
            it.summary = getString(R.string.app_name) +
                    getString(R.string.fragment_settings_version) +
                    BuildConfig.VERSION_NAME
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar_fragment_simple)
        toolbar?.let {
            setupToolbarWithNav(it)
            setupToolbarWithPadding(it)
        }
    }

    override fun onResume() {
        super.onResume()
        logScreenViewToFirebase()
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when (preference.key) {
            Constants.SWITCH_THEME_PREFERENCE_KEY -> {
                val nightModeOn = newValue as Boolean
                val themeMode = if (nightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "MODE_NIGHT_YES"
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "MODE_NIGHT_NO"
                }
                logPreferenceToFirebase(themeMode)
            }
            Constants.LIST_THEME_PREFERENCE_KEY -> {
                val value: Int = (newValue as String).toInt()
                AppCompatDelegate.setDefaultNightMode(value)
                val themeMode = when (value) {
                    -1 -> "MODE_NIGHT_FOLLOW_SYSTEM"
                    2 -> "MODE_NIGHT_YES"
                    1 -> "MODE_NIGHT_NO"
                    else -> "default (unchanged)"
                }
                logPreferenceToFirebase(themeMode)
            }
        }
        return true
    }

    private fun setupToolbarWithNav(toolbar: Toolbar) {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.charactersListFragment,
                R.id.locationsListFragment,
                R.id.episodesListFragment
        ))
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupToolbarWithPadding(toolbar: Toolbar){
        //set the toolbar title
        val toolbarTextView = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTextView.text = toolbar.title
        //apply padding to fit under translucent status bar
        setupEdgeToEdgePadding(toolbar)
    }

    private fun setupEdgeToEdgePadding(toolbar: Toolbar) {
        toolbar.setOnApplyWindowInsetsListener{ view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val systemWindow = insetsCompat.getInsets(
                    WindowInsetsCompat.Type.statusBars()
            )
            view.updatePadding(top = systemWindow.top)
            insets
        }
    }

    private fun logScreenViewToFirebase() {
        // fragment's class name for firebase logging
        val className = this.javaClass.simpleName
        //log screen view to firebase
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, className)
        }
    }

    private fun logPreferenceToFirebase(themeMode: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM){
            param(FirebaseAnalytics.Param.ITEM_NAME, themeMode)
        }
    }

}