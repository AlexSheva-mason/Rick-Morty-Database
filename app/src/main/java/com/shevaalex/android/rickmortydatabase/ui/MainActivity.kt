package com.shevaalex.android.rickmortydatabase.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.BottomNavViewModel
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.InitViewModel
import com.shevaalex.android.rickmortydatabase.ui.viewmodel.ReviewViewModel
import com.shevaalex.android.rickmortydatabase.utils.DiViewModelFactory
import com.shevaalex.android.rickmortydatabase.utils.networking.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<InitViewModel>

    @Inject
    lateinit var reviewViewmodelFactory: DiViewModelFactory<ReviewViewModel>

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectionStatus: ConnectionLiveData
    private var navController: NavController? = null
    private var backPressedOnce = false

    private val botNavViewModel: BottomNavViewModel by viewModels()
    private val initViewModel: InitViewModel by viewModels {
        viewModelFactory
    }
    private val reviewViewModel: ReviewViewModel by viewModels {
        reviewViewmodelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as RmApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        requestReviewInfo()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        databaseSyncCheck()
        setupNavController()
        registerObservers()
        setupEdgeToEdge()
    }

    private fun databaseSyncCheck() {
        //if database has been recently checked -> skip db sync
        initViewModel.isDbCheckNeeded().run {
            if (this) {
                monitorNetworkState()
                dbInit()
            }
        }
    }

    private fun dbInit() {
        initViewModel.init().observe(this, {
            it?.let { stateResource ->
                val snackColor: Int?
                when (stateResource.status) {
                    is Status.Error -> {
                        snackColor = ContextCompat.getColor(this, R.color.rm_red_add)
                        composeMessage(stateResource, snackColor)
                        binding.progressBar.progressBar.visibility = View.GONE
                    }
                    is Status.Loading -> {
                        composeMessage(stateResource)
                        binding.progressBar.progressBar.visibility = View.VISIBLE
                    }
                    is Status.Success -> {
                        snackColor = ContextCompat.getColor(this, R.color.rm_green_300)
                        composeMessage(stateResource, snackColor)
                        binding.progressBar.progressBar.visibility = View.GONE
                        //notify viewmodel Db sync success
                        initViewModel.notifyDbAllSuccess()
                        //notify reviewViewModel to increment the number of successful db sync events
                        reviewViewModel.notifyDbSyncSuccessful()
                        unSubscribe()
                    }
                }
            }
        })
    }

    private fun monitorNetworkState() {
        connectionStatus = ConnectionLiveData(this)
        connectionStatus.observe(this) {
            initViewModel.isNetworkAvailable(it)
        }
    }

    private fun unSubscribe() {
        connectionStatus.removeObservers(this)
        initViewModel.init().removeObservers(this)
    }


    private fun setupEdgeToEdge() {
        //set window to draw behind system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //update the padding of the BottomNavigationView
        binding.bottomPanel.setOnApplyWindowInsetsListener { view, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val systemWindow = insetsCompat.getInsets(
                    WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.ime()
            )
            view.updatePadding(bottom = systemWindow.bottom)
            insets
        }
    }

    private fun registerObservers() {
        //observe and set BottomNavigationView state
        botNavViewModel.bottomNavVisibility.observe(this,
                { integer: Int -> binding.bottomPanel.visibility = integer })
        botNavViewModel.bottomNavLabelVisibility.observe(this,
                { integer: Int -> binding.bottomPanel.labelVisibilityMode = integer })
    }

    private fun setupNavController() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomPanel.setupWithNavController(navController)
        // monitor navigation and set unlabeled BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            if (destination.id == R.id.settingsFragment) {
                botNavViewModel.hideBottomNav()
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

    private fun requestReviewInfo() {
        reviewViewModel.preWarmReview()
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
            Timer().schedule(2000) {
                backPressedOnce = false
            }
        } else {
            super.onBackPressed()
        }
    }

}