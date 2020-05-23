package com.shevaalex.android.rickmortydatabase.ui.location;

import android.app.Activity;
import android.content.Context;
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
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.ui.FragmentToolbarSimple;
import com.shevaalex.android.rickmortydatabase.ui.episode.CharacterAuxAdapter;

import java.util.ArrayList;
import java.util.List;

public class LocationDetailFragment extends FragmentToolbarSimple implements CharacterAuxAdapter.OnCharacterListener {
    private FragmentLocationDetailBinding binding;
    private CharacterAuxAdapter characterAuxAdapter;
    private LocationViewModel viewModel;
    private Activity a;
    private List<Character> mCharacterList = new ArrayList<>();

    public LocationDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            a = (Activity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(LocationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment;
        binding = FragmentLocationDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        String dimension = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationDimension();
        String type = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationType();
        String name = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationName();
        if (name.length() >= 30) {
            binding.partialToolbarSimple.toolbarFragmentSimple.setTitleTextAppearance(a.getApplication().getApplicationContext(), R.style.TextAppearance_RM_Toolbar_Collapsed_Title_Small);
        }
        int locationId = LocationDetailFragmentArgs.fromBundle(requireArguments()).getLocationId();
        //set retreived data to appropriate views
        binding.locationDimensionValue.setText(dimension);
        binding.locationTypeValue.setText(type);
        //set the recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewLocationDetail.setLayoutManager(layoutManager);
        binding.recyclerviewLocationDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        characterAuxAdapter = new CharacterAuxAdapter(this);
        characterAuxAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewLocationDetail.setAdapter(characterAuxAdapter);
        viewModel.getCharactersFromLocation(locationId).observe(getViewLifecycleOwner(), characters -> {
            if (!characters.isEmpty()) {
                characterAuxAdapter.setCharacterList(characters);
                mCharacterList = characters;
            } else {
                binding.locationResidentsNone.setVisibility(View.VISIBLE);
            }
        });
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

    @Override
    public void onCharacterClick(int position, View v) {
        if (mCharacterList != null && !mCharacterList.isEmpty()) {
            Character clickedChar = mCharacterList.get(position);
            LocationDetailFragmentDirections.ToCharacterDetailFragmentAction3 action =
                    LocationDetailFragmentDirections.toCharacterDetailFragmentAction3();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
