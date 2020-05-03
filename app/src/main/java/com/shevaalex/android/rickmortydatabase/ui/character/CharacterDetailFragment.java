package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.squareup.picasso.Picasso;

@SuppressWarnings("WeakerAccess")
public class CharacterDetailFragment extends Fragment implements View.OnClickListener {
    private FragmentCharacterDetailBinding binding;
    private CharacterViewModel viewModel;
    private Activity a;
    private Location originLocation;
    private Location lastLocation;

    public CharacterDetailFragment() {
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
        viewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(CharacterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        String imgUrl = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getImageUrl();
        String charStatus = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterStatus();
        String charSpecies = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterSpecies();
        String charType = "";
        if (!CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterType().isEmpty()) {
            charType = getString(R.string.character_type_placeholder, CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterType());
        }
        String charGender = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterGender();
        int charOriginID = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterOrigin();
        int charLastLocID = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterLastLocation();



        binding.buttonOriginLocation.setOnClickListener(this);
        binding.buttonLastLocation.setOnClickListener(this);
        if (charOriginID != 0) {
            binding.characterOriginValue.setVisibility(View.GONE);
            originLocation = viewModel.getLocationById(charOriginID);
            binding.buttonOriginLocation.setText(originLocation.getName());
        } else {
            binding.buttonOriginLocation.setVisibility(View.GONE);
            binding.characterOriginValue.setVisibility(View.VISIBLE);
        }
        if (charLastLocID != 0) {
            binding.characterLastLocationValue.setVisibility(View.GONE);
            lastLocation = viewModel.getLocationById(charLastLocID);
            binding.buttonLastLocation.setText(lastLocation.getName());
        } else {
            binding.buttonLastLocation.setVisibility(View.GONE);
            binding.characterLastLocationValue.setVisibility(View.VISIBLE);
        }



        //set retreived data to appropriate views
        Picasso.get().load(imgUrl).placeholder(R.drawable.picasso_placeholder_default).error(R.drawable.picasso_placeholder_error).
                fit().centerInside().into(binding.characterImage);
        binding.characterStatusValue.setText(charStatus);
        binding.characterSpeciesValue.setText(charSpecies);
        if (!charType.isEmpty()) {
            binding.characterTypeValue.setText(charType);
        } else {
            binding.characterTypeValue.setVisibility(View.GONE);
        }
        binding.characterGenderValue.setText(charGender);
        //binding.characterOriginValue.setText(String.valueOf(charOriginID));
        //binding.characterLastLocationValue.setText(String.valueOf(charLastLocID));
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {

    }
}
