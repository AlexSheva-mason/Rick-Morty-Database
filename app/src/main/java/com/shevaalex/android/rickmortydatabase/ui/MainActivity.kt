package com.shevaalex.android.rickmortydatabase.ui

import android.os.Bundle
import android.view.View
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
import com.shevaalex.android.rickmortydatabase.utils.networking.Message
import com.shevaalex.android.rickmortydatabase.utils.networking.StateResource
import com.shevaalex.android.rickmortydatabase.utils.networking.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: DiViewModelFactory<InitViewModel>

    @Inject
    lateinit var reviewViewmodelFactory: DiViewModelFactory<ReviewViewModel>

    private lateinit var binding: ActivityMainBinding
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
        setupNavController()
        registerObservers()
        setupEdgeToEdge()
    }

    private fun registerObservers() {
        //initialise or update the local database
        dbInit()
        //observe and set BottomNavigationView state
        botNavViewModel.bottomNavVisibility.observe(this,
            { integer: Int -> binding.bottomPanel.visibility = integer })
        botNavViewModel.bottomNavLabelVisibility.observe(this,
            { integer: Int -> binding.bottomPanel.labelVisibilityMode = integer })
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
                        unSubscribe()
                    }
                }
            }
        })
    }

    private fun unSubscribe() {
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

    private fun setupNavController() {
        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomPanel.setupWithNavController(navController)
        // monitor navigation and set unlabeled BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            if (destination.id == R.id.settingsFragment) {
                botNavViewModel.hideBottomNav()
            } else if (destination.id == R.id.characterDetailFragment2
                || destination.id == R.id.locationDetailFragment
                || destination.id == R.id.episodeDetailFragment
            ) {
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
        when (stateResource.message) {
            is Message.NoInternet -> {
                snackText = getString(R.string.ma_snack_database_not_synced)
            }
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
            showSnackBar(snackText, snackColor)
        }
    }

    private fun showSnackBar(text: String, color: Int? = null) {
        val mySnackbar = Snackbar
            .make(binding.activityMainLayout, text, BaseTransientBottomBar.LENGTH_LONG)
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
            && navController?.graph?.startDestination == navController?.currentDestination?.id
        ) {
            if (backPressedOnce) {
                super.onBackPressed()
                return
            }
            backPressedOnce = true
            showSnackBar(resources.getString(R.string.toast_close_message))
            Timer().schedule(2000) {
                backPressedOnce = false
            }
        } else {
            super.onBackPressed()
        }
    }

}