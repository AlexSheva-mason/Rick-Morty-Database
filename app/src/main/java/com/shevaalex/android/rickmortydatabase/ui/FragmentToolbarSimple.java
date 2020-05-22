package com.shevaalex.android.rickmortydatabase.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.view.View;

import com.shevaalex.android.rickmortydatabase.R;


public abstract class FragmentToolbarSimple extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.charactersListFragment, R.id.locationsListFragment, R.id.episodesListFragment).build();
        Toolbar toolbar = view.findViewById(R.id.toolbar_fragment_simple);
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
    }
}
