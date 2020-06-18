package com.shevaalex.android.rickmortydatabase.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.ActivityMainBinding;
import com.shevaalex.android.rickmortydatabase.ui.character.CharacterViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static ArrayList<String> snackMessages = new ArrayList<>();
    private ActivityMainBinding binding;
    private NavController navController;
    private boolean backPressedOnce;
    private BottomNavViewModel botNavViewModel;
    private CharacterViewModel characterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        botNavViewModel = new ViewModelProvider(this).get(BottomNavViewModel.class);
        characterViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication()).create(CharacterViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setupViews();
        monitorConnectionAndDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupViews() {
        // Finding the navigation controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // Setting the nav controller with bottom navigation
        NavigationUI.setupWithNavController(binding.bottomPanel, navController);
        //setup the ViewModel for lifecycle aware observing bottomNav state
        botNavViewModel.getBottomNavVisibility().observe(this, integer -> binding.bottomPanel.setVisibility(integer));
        botNavViewModel.getBottomNavLabelStatus().observe(this, integer -> binding.bottomPanel.setLabelVisibilityMode(integer));
        // monitor navigation and remove BottomNavigationView in Detail fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.settingsFragment || destination.getId() == R.id.characterImageFragment) {
                new Handler().postDelayed(() ->
                        botNavViewModel.hideBottomNav(), 100);
            } else if(destination.getId() == R.id.characterDetailFragment2 || destination.getId() == R.id.locationDetailFragment
                    || destination.getId() == R.id.episodeDetailFragment) {
                botNavViewModel.showBottomNav();
                botNavViewModel.setUnlabeled();
            } else {
                botNavViewModel.setLabelSelected();
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

    //monitors internet connection, checks if database is up to date
    private void monitorConnectionAndDatabase() {
        characterViewModel.getStatusLiveData().observe(this, pair -> {
            @NonNull String text;
            int snackBarDuration = BaseTransientBottomBar.LENGTH_SHORT;
            // database is up to date and device is connected to network
            if (pair.first && pair.second) {
                binding.progressBar.progressBar.setVisibility(View.GONE);
                text = getString(R.string.ma_snack_database_up_to_date);
            }
            // database is _not_ up to date and device is connected to network
            else if (!pair.first && pair.second) {
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
                characterViewModel.rmRepository.initialiseDataBase();
                text = getString(R.string.ma_snack_database_sync);
            }
            // database is up to date and device is _disconnected_ from network
            else if (pair.first) {
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
                text = getString(R.string.ma_snack_database_up_to_date);
            }
            // database is _not_ up to date and device is _disconnected_ from network
            else {
                binding.progressBar.progressBar.setVisibility(View.VISIBLE);
                snackBarDuration = BaseTransientBottomBar.LENGTH_INDEFINITE;
                text = getString(R.string.ma_snack_database_not_synced);
            }
            showSnackBar(text, snackBarDuration);
        });
    }

    private void showSnackBar(String text, int snackBarDuration) {
        if (!text.isEmpty() && !snackMessages.contains(text)) {
            Snackbar mySnackbar = Snackbar.make(binding.activityMainLayout, text, snackBarDuration);
            mySnackbar.setTextColor(getResources().getColor(R.color.rm_white_50));
            mySnackbar.setAnchorView(binding.bottomPanel);
            mySnackbar.show();
            if (!text.equals(getString(R.string.ma_snack_database_not_synced))) {
                snackMessages.add(text);
            }
        }
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
