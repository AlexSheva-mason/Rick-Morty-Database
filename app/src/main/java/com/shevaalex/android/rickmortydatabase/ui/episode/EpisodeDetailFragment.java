package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodeDetailBinding;

public class EpisodeDetailFragment extends Fragment implements CharacterAuxAdapter.OnCharacterListener {
    private FragmentEpisodeDetailBinding binding;
    private CharacterAuxAdapter characterAuxAdapter;
    private EpisodeViewModel viewModel;
    private Activity a;

    public EpisodeDetailFragment() {
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
        viewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(EpisodeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment;
        binding = FragmentEpisodeDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        String name = EpisodeDetailFragmentArgs.fromBundle(requireArguments()).getEpisodeName();
        String airDate = EpisodeDetailFragmentArgs.fromBundle(requireArguments()).getEpisodeAirDate();
        int episodeID = EpisodeDetailFragmentArgs.fromBundle(requireArguments()).getId();
        Log.d("TAG", "onCreateView: epID " + episodeID);
        //set retreived data to appropriate views
        binding.episodeNameValue.setText(name);
        binding.episodeAirDateValue.setText(airDate);
        //set the recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewEpisodeDetail.setLayoutManager(layoutManager);
        binding.recyclerviewEpisodeDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        characterAuxAdapter = new CharacterAuxAdapter(this);
        binding.recyclerviewEpisodeDetail.setAdapter(characterAuxAdapter);
        viewModel.getCharactersFromEpisode(episodeID).observe(getViewLifecycleOwner(), characters -> characterAuxAdapter.setCharacterList(characters));
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

    }
}
