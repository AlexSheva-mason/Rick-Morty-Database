package com.shevaalex.android.rickmortydatabase.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Pair
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.ActivityMainBinding
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_ACTIVITY_MAIN_DB_SYNC_BOOL
import com.shevaalex.android.rickmortydatabase.utils.MyViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.networking.ConnectionLiveData
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private val snackMessages = ArrayList<String>()
        var defSystemLanguage = Locale.getDefault().language
    }

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory<InitViewModel>

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionStatus: ConnectionLiveData
    private var navController: NavController? = null
    private var backPressedOnce = false

    private val botNavViewModel: BottomNavViewModel by viewModels()
    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()
    private val initViewModel: InitViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as RmApplication).appComponent.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        connectionStatus = ConnectionLiveData(this)
        FirebaseAnalytics.getInstance(this)
        restoreInstanceState(savedInstanceState)
        setupViews()
        monitorConnectionAndDatabase()
        getInitState()
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let{savedInstance ->
            (savedInstance[KEY_ACTIVITY_MAIN_DB_SYNC_BOOL] as Boolean?)?.let {dbSynced ->
                initViewModel.dbIsSynced(dbSynced)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        val dbIsSynced = initViewModel.dbIsSynced.value?: false
        outState.putBoolean(KEY_ACTIVITY_MAIN_DB_SYNC_BOOL, dbIsSynced)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun getInitState() {
        initViewModel.dbIsSynced.observe(this, { isSynced ->
            isSynced?.let {
                //if db has not been synced -> monitor network connection and fetch data when connected
                if (!it) {
                    monitorNetworkState()
                    testFlow()
                }
            }
        })
    }

    private fun testFlow() {
        initViewModel.test.observe(this, {
            if (it != null) {
                Timber.i("stateResource is not null")
            } else {
                Timber.e("stateResource is null")
            }
            it?.let {stateResource ->
                when (stateResource.status) {
                    is Status.Error -> Timber.e("Error")
                    is Status.Loading -> Timber.i("Loading")
                    is Status.Success -> {
                        Timber.d("Success!")
                        initViewModel.dbIsSynced(true)
                        unSubscribe()
                    }
                }
                when (stateResource.message) {
                    is Message.NoInternet -> Timber.d("NoInternet")
                    is Message.UpdatingDatabase -> Timber.i("UpdatingDatabase")
                    is Message.DbIsUpToDate -> Timber.i("DbIsUpToDate")
                    is Message.ServerError -> Timber.e(
                            "ServerError: %s",
                            stateResource.message.statusCode
                    )
                    is Message.NetworkError -> Timber.e("NetworkError")
                    is Message.EmptyResponse -> Timber.e("EmptyResponse")
                    null -> Timber.i("empty message")
                }
            }
        })
    }

    private fun unSubscribe() {
        connectionStatus.removeObservers(this)
        initViewModel.test.removeObservers(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //reinit database if locale has been changed
        if (defSystemLanguage != newConfig.locale.language) {
            defSystemLanguage = newConfig.locale.language
            //do stuff
        }
    }

    private fun monitorNetworkState() {
        //initViewModel.isNetworkAvailable(false)
        connectionStatus.observe(this) {
            initViewModel.isNetworkAvailable(it)
        }
    }

    private fun setupViews() {
        // Finding the navigation controller
        val navController = findNavController(R.id.nav_host_fragment)
        // Setting the nav controller with bottom navigation
        NavigationUI.setupWithNavController(binding.bottomPanel, navController)
        //setup the ViewModel for lifecycle aware observing bottomNav state
        botNavViewModel.bottomNavVisibility.observe(this,
                { integer: Int -> binding.bottomPanel.visibility = integer })
        botNavViewModel.bottomNavLabelStatus.observe(this,
                { integer: Int -> binding.bottomPanel.labelVisibilityMode = integer })
        // monitor navigation and remove BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
            if (destination.id == R.id.settingsFragment
                    || destination.id == R.id.characterImageFragment) {
                Handler().postDelayed({ botNavViewModel.hideBottomNav() }, 100)
            } else if (destination.id == R.id.characterDetailFragment2 || destination.id == R.id.locationDetailFragment || destination.id == R.id.episodeDetailFragment) {
                botNavViewModel.showBottomNav()
                botNavViewModel.setUnlabeled()
            } else {
                botNavViewModel.setLabelSelected()
                botNavViewModel.showBottomNav()
            }
        }
        // add bottom menu listener to prevent posibbility of double clicking the same item and refreshing or backing up the old search
        binding.bottomPanel.setOnNavigationItemSelectedListener { item ->
            if (navController.currentDestination != null
                    && navController.currentDestination!!.id != item.itemId) {
                NavigationUI.onNavDestinationSelected(item, navController)
                return@setOnNavigationItemSelectedListener true
            }
            true
        }
    }

    //TODO delete redundant method
    //monitors internet connection, checks if database is up to date
    private fun monitorConnectionAndDatabase() {
        networkStatusViewModel.networkStatusLiveData.observe(this, { pair: Pair<Boolean, Boolean> ->
            val text: String
            var snackBarDuration = BaseTransientBottomBar.LENGTH_SHORT
            // database is up to date and device is connected to network
            if (pair.first && pair.second) {
                binding.progressBar.progressBar.visibility = View.GONE
                text = getString(R.string.ma_snack_database_up_to_date)
            } else if (!pair.first && pair.second) {
                binding.progressBar.progressBar.visibility = View.VISIBLE
                //**characterViewModel.rmRepository.initialiseDataBase();
                text = getString(R.string.ma_snack_database_sync)
            } else if (pair.first) {
                text = getString(R.string.ma_snack_database_up_to_date)
            } else {
                binding.progressBar.progressBar.visibility = View.VISIBLE
                snackBarDuration = BaseTransientBottomBar.LENGTH_INDEFINITE
                text = getString(R.string.ma_snack_database_not_synced)
            }
            showSnackBar(text, snackBarDuration)
        })
    }

    private fun showSnackBar(text: String, snackBarDuration: Int) {
        if (text.isNotEmpty() && !snackMessages.contains(text)) {
            val mySnackbar = Snackbar.make(binding.activityMainLayout, text, snackBarDuration)
            val snackBarView = mySnackbar.view
            //TODO set the error color to red when appropriate
            //snackBarView.getRootView().setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            mySnackbar.setTextColor(resources.getColor(R.color.rm_white_50))
            mySnackbar.setAnchorView(binding.bottomPanel)
            mySnackbar.show()
            if (text != getString(R.string.ma_snack_database_not_synced)) {
                snackMessages.add(text)
            }
        }
    }

    override fun onBackPressed() {
        if (navController == null) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        }
        // Check if the current destination is actually the start destination (Home screen)
        if (navController!!.currentDestination != null
                && navController!!.graph.startDestination == navController!!.currentDestination!!.id) {
            if (backPressedOnce) {
                super.onBackPressed()
                return
            }
            backPressedOnce = true
            Toast.makeText(this, resources.getString(R.string.toast_close_message), Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ backPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
        }
    }

}