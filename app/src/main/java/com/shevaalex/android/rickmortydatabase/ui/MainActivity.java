package com.shevaalex.android.rickmortydatabase.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private boolean backPressedOnce;
    private BottomNavViewModel botNavViewModel;
    private static boolean uiIsShown;
    private View decorView;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        decorView = getWindow().getDecorView();
        actionBar = getSupportActionBar();
        setupViews();
    }

    private void setupViews() {
        // Finding the navigation controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Setting the nav controller with bottom navigation
        NavigationUI.setupWithNavController(binding.bottomPanel, navController);
        //setup the ViewModel for lifecycle aware observing bottomNav state
        botNavViewModel = new ViewModelProvider(this).get(BottomNavViewModel.class);
        botNavViewModel.getBottomNavVisibility().observe(this, integer -> binding.bottomPanel.setVisibility(integer));
        // monitor navigation and remove BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.characterDetailFragment2 || destination.getId() == R.id.locationDetailFragment
                || destination.getId() == R.id.episodeDetailFragment || destination.getId() == R.id.settingsFragment) {
                new Handler().postDelayed(() ->
                        botNavViewModel.hideBottomNav(), 100);
            } else if (destination.getId() == R.id.splashFragment){
                if (uiIsShown) {
                    hideUi();
                }
                botNavViewModel.hideBottomNav();
            } else {
                if (!uiIsShown) {
                    showUi();
                }
                botNavViewModel.showBottomNav();
            }
        });
        // add bottom menu listener to prevent posibbility of double clicking the same item and refreshing or backing up the old search
        binding.bottomPanel.setOnNavigationItemSelectedListener(item -> {
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != item.getItemId()){
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
            return true;
        });
    }

    private void hideUi() {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        if (actionBar != null) {
            actionBar.hide();
        }
        uiIsShown = false;
    }

    private void showUi() {
        decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_VISIBLE);
        if (actionBar != null) {
            actionBar.show();
        }
        uiIsShown = true;
    }

    @Override
    public void onBackPressed() {
        if(navController == null) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        }
        // Check if the current destination is actually the start destination (Home screen)
        if (navController.getCurrentDestination() != null
                && navController.getGraph().getStartDestination() == navController.getCurrentDestination().getId()){
            if(backPressedOnce){
                super.onBackPressed();
                return;
            }
            backPressedOnce = true;
            Toast.makeText(this, "Tap BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> backPressedOnce = false, 2000);
        } else { super.onBackPressed(); }
    }

}
