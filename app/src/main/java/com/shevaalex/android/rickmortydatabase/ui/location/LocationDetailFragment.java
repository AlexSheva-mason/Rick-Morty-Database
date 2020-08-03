package com.shevaalex.android.rickmortydatabase.ui.location;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentLocationDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.ui.FragmentToolbarSimple;
import com.shevaalex.android.rickmortydatabase.ui.character.CharacterAuxAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailFragment extends FragmentToolbarSimple implements CharacterAuxAdapter.OnCharacterListener {
    private FragmentLocationDetailBinding binding;
    private CharacterAuxAdapter characterAuxAdapter;
    private LocationDetailViewModel locationDetailViewModel;
    private List<CharacterSmall> mCharacterList = new ArrayList<>();
    private Context context;

    public LocationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationDetailViewModel = new ViewModelProvider(this).get(LocationDetailViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment;
        binding = FragmentLocationDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        String name = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationName();
        String dimension = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationDimension();
        String type = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationType();
        int locationId = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationId();
        //save location id with SavedStateHandle
        locationDetailViewModel.setLocationId(locationId);
        //set retreived data to appropriate views
        if (binding.locationNameValue != null) {
            binding.locationNameValue.setText(name);
        }
        binding.locationDimensionValue.setText(dimension);
        binding.locationTypeValue.setText(type);
        setRecyclerView();
        registerObservers();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (characterAuxAdapter != null) {
            characterAuxAdapter = null;
        }
    }

    private void setRecyclerView() {
        //set the recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        }
        binding.recyclerviewLocationDetail.setLayoutManager(layoutManager);
        binding.recyclerviewLocationDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        characterAuxAdapter = new CharacterAuxAdapter(this, context);
        characterAuxAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    private void registerObservers() {
        locationDetailViewModel.getCharactersFromLocation().observe(getViewLifecycleOwner(), characters -> {
            if (!characters.isEmpty()) {
                characterAuxAdapter.setCharacterList(characters);
                mCharacterList = characters;
                binding.recyclerviewLocationDetail.setAdapter(characterAuxAdapter);
            } else {
                binding.locationResidentsNone.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCharacterClick(int position, View v) {
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            CharacterSmall clickedChar = mCharacterList.get(position);
            LocationDetailFragmentDirections.ToCharacterDetailFragmentAction3 action =
                    LocationDetailFragmentDirections.toCharacterDetailFragmentAction3();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
