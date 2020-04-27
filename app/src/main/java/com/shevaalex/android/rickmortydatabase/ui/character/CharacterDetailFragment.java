package com.shevaalex.android.rickmortydatabase.ui.character;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;
import com.squareup.picasso.Picasso;

@SuppressWarnings("WeakerAccess")
public class CharacterDetailFragment extends Fragment {
    private FragmentCharacterDetailBinding binding;

    public CharacterDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        /*String charName = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterName();*/
        String imgUrl = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getImageUrl();
        String charStatus = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterStatus();
        String charSpecies = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterSpecies();
        String charType = "";
        if (!CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterType().isEmpty()) {
            charType = getString(R.string.character_type_placeholder, CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterType());
        }
        String charGender = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterGender();
        int charOrigin = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterOrigin();
        int charLastLoc = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterLastLocation();
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
        binding.characterOriginValue.setText(String.valueOf(charOrigin));
        binding.characterLastLocationValue.setText(String.valueOf(charLastLoc));
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
