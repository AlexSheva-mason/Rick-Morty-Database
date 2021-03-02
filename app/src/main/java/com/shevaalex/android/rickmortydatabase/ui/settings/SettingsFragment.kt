package com.shevaalex.android.rickmortydatabase.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.preference.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.shevaalex.android.rickmortydatabase.BuildConfig
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.utils.Constants
import com.shevaalex.android.rickmortydatabase.utils.FirebaseLogger
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
        PreferenceManager.OnPreferenceTreeClickListener {

    @Inject
    lateinit var firebaseLogger: FirebaseLogger

    override fun onAttach(context: Context) {
        super.onAttach(context)
        injectFragment()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val iconTintColor = requireActivity().getColor(R.color.material_on_surface_disabled)
        val switchPreference =
                findPreference<SwitchPreferenceCompat>(Constants.SWITCH_THEME_PREFERENCE_KEY)
        val listPreference = findPreference<ListPreference>(Constants.LIST_THEME_PREFERENCE_KEY)
        val versionPreference = findPreference<Preference>(Constants.KEY_VERSION)
        val reviewPreference = findPreference<Preference>(Constants.KEY_REVIEW)
        switchPreference?.let {
            it.icon.setTint(iconTintColor)
            it.onPreferenceChangeListener = this
        }
        listPreference?.let {
            it.icon.setTint(iconTintColor)
            it.onPreferenceChangeListener = this
        }
        versionPreference?.let {
            it.icon.setTint(iconTintColor)
            it.summary = getString(R.string.app_name) +
                    getString(R.string.fragment_settings_version) +
                    BuildConfig.VERSION_NAME
        }
        reviewPreference?.let {
            it.icon.setTint(iconTintColor)
            val webLinkIntent = Intent()
            webLinkIntent.action = Intent.ACTION_VIEW
            webLinkIntent.data = Uri.parse(Constants.DATA_REVIEW_LINK)
            it.intent = webLinkIntent
        }
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
                logThemeModeToFirebase(themeMode)
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
                logThemeModeToFirebase(themeMode)
            }
        }
        return true
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val key = preference?.key
        var prefClicked: String? = null
        when (key) {
            Constants.SWITCH_THEME_PREFERENCE_KEY -> prefClicked = Constants.SWITCH_THEME_PREFERENCE_KEY
            Constants.LIST_THEME_PREFERENCE_KEY -> prefClicked = Constants.LIST_THEME_PREFERENCE_KEY
            Constants.KEY_REVIEW -> prefClicked = Constants.KEY_REVIEW
        }
        prefClicked?.let { logPreferenceClickToFirebase(it) }
        return super.onPreferenceTreeClick(preference)
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

    private fun setupToolbarWithPadding(toolbar: Toolbar) {
        //set the toolbar title
        val toolbarTextView = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTextView.text = toolbar.title
        //apply padding to fit under translucent status bar
        setupEdgeToEdgePadding(toolbar)
    }

    private fun setupEdgeToEdgePadding(toolbar: Toolbar) {
        toolbar.setOnApplyWindowInsetsListener { view, insets ->
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
        firebaseLogger.logFirebaseEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                FirebaseAnalytics.Param.SCREEN_CLASS,
                className
        )
    }

    private fun logThemeModeToFirebase(themeMode: String) {
        firebaseLogger.logFirebaseEvent(
                Constants.SETTINGS_EVENT_THEME_SELECT,
                Constants.SETTINGS_KEY_THEME_MODE,
                themeMode
        )
    }

    private fun logPreferenceClickToFirebase(prefName: String) {
        firebaseLogger.logFirebaseEvent(
                Constants.SETTINGS_EVENT_PREFERENCE_CLICK,
                Constants.SETTINGS_KEY_PREFERENCE_NAME,
                prefName
        )
    }

    private fun injectFragment() {
        activity?.run {
            (application as RmApplication).appComponent
        }?.inject(this)
    }

}