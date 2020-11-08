package com.shevaalex.android.rickmortydatabase.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.shevaalex.android.rickmortydatabase.R
import com.shevaalex.android.rickmortydatabase.RmApplication
import com.shevaalex.android.rickmortydatabase.databinding.ActivityMainBinding
import com.shevaalex.android.rickmortydatabase.utils.Constants.Companion.KEY_ACTIVITY_MAIN_DB_SYNC_BOOL
import com.shevaalex.android.rickmortydatabase.utils.MyViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.networking.ConnectionLiveData
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory<InitViewModel>

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionStatus: ConnectionLiveData
    private var navController: NavController? = null
    private var backPressedOnce = false

    private val botNavViewModel: BottomNavViewModel by viewModels()
    private val initViewModel: InitViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as RmApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        connectionStatus = ConnectionLiveData(this)
        restoreInstanceState(savedInstanceState)
        setupViews()
        getInitState()
    }

    private fun restoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let{savedInstance ->
            Timber.v("restoreInstanceState: savedInstanceState not null")
            (savedInstance[KEY_ACTIVITY_MAIN_DB_SYNC_BOOL] as Boolean?)?.let {dbSynced ->
                Timber.v("restoring dbsynced bool: %s", dbSynced)
                initViewModel.dbIsSynced(dbSynced)
            }
        }?: Timber.v("restoreInstanceState: savedInstanceState is null")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val dbIsSynced = initViewModel.dbIsSynced.value?: false
        outState.putBoolean(KEY_ACTIVITY_MAIN_DB_SYNC_BOOL, dbIsSynced)
    }

    private fun getInitState() {
        initViewModel.dbIsSynced.observe(this, { isSynced ->
            isSynced?.let {
                //if db has not been synced -> monitor network connection and fetch data when connected
                if (!it) {
                    monitorNetworkState()
                    dbInit()
                }
            }
        })
    }

    private fun dbInit() {
        initViewModel.test.observe(this, {
            it?.let {stateResource ->
                val snackColor: Int?
                when (stateResource.status) {
                    is Status.Error -> {
                        snackColor = ContextCompat.getColor(this, R.color.rm_red_add)
                        composeMessage(stateResource, snackColor)
                        Timber.e("Error")
                        binding.progressBar.progressBar.visibility = View.GONE
                    }
                    is Status.Loading -> {
                        composeMessage(stateResource)
                        binding.progressBar.progressBar.visibility = View.VISIBLE
                    }
                    is Status.Success -> {
                        snackColor = ContextCompat.getColor(this, R.color.rm_green_300)
                        composeMessage(stateResource, snackColor)
                        Timber.d("Success!")
                        binding.progressBar.progressBar.visibility = View.GONE
                        initViewModel.dbIsSynced(true)
                        unSubscribe()
                    }
                }
            }
        })
    }

    private fun monitorNetworkState() {
        //initViewModel.isNetworkAvailable(false)
        connectionStatus.observe(this) {
            initViewModel.isNetworkAvailable(it)
        }
    }

    private fun unSubscribe() {
        connectionStatus.removeObservers(this)
        initViewModel.test.removeObservers(this)
    }

    private fun setupViews() {
        // Finding the navigation controller
        val navController = findNavController(R.id.nav_host_fragment)
        // Setting the nav controller with bottom navigation
        binding.bottomPanel.setupWithNavController(navController)
        //setup the ViewModel for lifecycle aware observing bottomNav state
        botNavViewModel.bottomNavVisibility.observe(this,
                { integer: Int -> binding.bottomPanel.visibility = integer })
        botNavViewModel.bottomNavLabelStatus.observe(this,
                { integer: Int -> binding.bottomPanel.labelVisibilityMode = integer })
        // monitor navigation and remove BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            if (destination.id == R.id.settingsFragment
                    || destination.id == R.id.characterImageFragment) {
                Timer().schedule(100){
                    botNavViewModel.hideBottomNav()
                }
            } else if (destination.id == R.id.characterDetailFragment2
                    || destination.id == R.id.locationDetailFragment
                    || destination.id == R.id.episodeDetailFragment) {
                botNavViewModel.showBottomNav()
                botNavViewModel.setUnlabeled()
            } else {
                botNavViewModel.showBottomNav()
                botNavViewModel.setLabelSelected()
            }
        }
        // add bottom menu listener to prevent posibbility of double clicking the same item
        // and refreshing or backing up the old search
        binding.bottomPanel.setOnNavigationItemReselectedListener {
            // do nothing
        }
    }

    private fun composeMessage(stateResource: StateResource, snackColor: Int? = null) {
        val snackText: String
        var snackBarDuration = BaseTransientBottomBar.LENGTH_LONG
        when (stateResource.message) {
            is Message.NoInternet -> {
                snackBarDuration = BaseTransientBottomBar.LENGTH_INDEFINITE
                snackText = getString(R.string.ma_snack_database_not_synced)
            }
            is Message.UpdatingDatabase ->
                snackText = getString(R.string.ma_snack_database_sync)
            is Message.DbIsUpToDate ->
                snackText = getString(R.string.ma_snack_database_up_to_date)
            is Message.ServerError ->
                snackText = getString(R.string.ma_snack_error_server_error)
                        .plus(stateResource.message.statusCode)
            is Message.NetworkError ->
                snackText = getString(R.string.ma_snack_error_network_error)
            is Message.EmptyResponse ->
                snackText = getString(R.string.ma_snack_error_empty_response)
            null ->
                snackText = ""
        }
        if (snackText.isNotBlank()) {
            showSnackBar(snackText, snackBarDuration, snackColor)
        }
    }

    private fun showSnackBar(text: String, snackBarDuration: Int, color: Int?) {
        val mySnackbar = Snackbar.make(binding.activityMainLayout, text, snackBarDuration)
        val snackBarView = mySnackbar.view
        color?.let {
            snackBarView.rootView.setBackgroundColor(it)
        }
        mySnackbar.setTextColor(ContextCompat.getColor(this, R.color.rm_white_50))
        mySnackbar.anchorView = binding.bottomPanel
        mySnackbar.show()
    }

    override fun onBackPressed() {
        if (navController == null) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        }
        // Check if the current destination is actually the start destination (Home screen)
        if (navController?.currentDestination != null
                && navController?.graph?.startDestination == navController?.currentDestination?.id) {
            if (backPressedOnce) {
                super.onBackPressed()
                return
            }
            backPressedOnce = true
            Toast.makeText(
                    this,
                    resources.getString(R.string.toast_close_message),
                    Toast.LENGTH_SHORT)
                    .show()
            Timer().schedule(2000){
                backPressedOnce = false
            }
        } else {
            super.onBackPressed()
        }
    }

}