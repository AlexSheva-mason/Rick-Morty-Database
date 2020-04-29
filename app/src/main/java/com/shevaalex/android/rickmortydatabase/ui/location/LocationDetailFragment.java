package com.shevaalex.android.rickmortydatabase.ui.location;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationDetailBinding;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("WeakerAccess")
public class LocationDetailFragment extends Fragment {
    private FragmentLocationDetailBinding binding;

    public LocationDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment;
        binding = FragmentLocationDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        String dimension = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationDimension();
        String type = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationType();
        String residents = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationResidents();
        //set retreived data to appropriate views
        binding.locationDimensionValue.setText(dimension);
        binding.locationTypeValue.setText(type);
        binding.locationResidentsValue.setText(residents);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
