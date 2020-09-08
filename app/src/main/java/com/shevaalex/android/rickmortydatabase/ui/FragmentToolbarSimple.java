package com.shevaalex.android.rickmortydatabase.ui;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.view.View;
import android.widget.TextView;

import com.shevaalex.android.rickmortydatabase.R;


public abstract class FragmentToolbarSimple extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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
    }

}
