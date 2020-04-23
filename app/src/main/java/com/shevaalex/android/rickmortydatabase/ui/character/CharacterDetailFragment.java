package com.shevaalex.android.rickmortydatabase.ui.character;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;

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
        // retrieve data from parent fragment and set it to appropriate views
        String charName = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getCharacterName();
        String imgUrl = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getImageUrl();
        /*binding.characterName.setText(charName);
        binding.characterImgUrl.setText(imgUrl);*/
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
