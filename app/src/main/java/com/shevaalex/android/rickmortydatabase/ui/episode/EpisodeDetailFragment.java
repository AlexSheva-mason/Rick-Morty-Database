package com.shevaalex.android.rickmortydatabase.ui.episode;

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

import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodeDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.ui.FragmentToolbarSimple;
import com.shevaalex.android.rickmortydatabase.ui.character.CharacterAuxAdapter;

import java.util.ArrayList;
import java.util.List;

public class EpisodeDetailFragment extends FragmentToolbarSimple
        implements CharacterAuxAdapter.OnCharacterListener {
    private FragmentEpisodeDetailBinding binding;
    private CharacterAuxAdapter characterAuxAdapter;
    private EpisodeDetailViewModel episodeDetailViewModel;
    private List<CharacterSmall> characterList = new ArrayList<>();
    private Context context;

    public EpisodeDetailFragment() {
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
        episodeDetailViewModel = new ViewModelProvider(this)
                .get(EpisodeDetailViewModel.class);
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
        //save episode Id with SavedStateHandle
        episodeDetailViewModel.setEpisodeId(episodeID);
        //set retreived data to appropriate views
        binding.episodeNameValue.setText(name);
        binding.episodeAirDateValue.setText(airDate);
        //set the recyclerview
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        }
        binding.recyclerviewEpisodeDetail.setLayoutManager(layoutManager);
        binding.recyclerviewEpisodeDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        characterAuxAdapter = new CharacterAuxAdapter(context, this);
        characterAuxAdapter
                .setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    private void registerObservers() {
        episodeDetailViewModel
                .getCharactersFromEpisode()
                .observe(getViewLifecycleOwner(), characters -> {
            characterList = characters;
            characterAuxAdapter.setCharacterList(characters);
            binding.recyclerviewEpisodeDetail.setAdapter(characterAuxAdapter);
        });
    }

    @Override
    public void onCharacterClick(int position, View v) {
        if (characterList != null && !characterList.isEmpty()) {
            CharacterSmall clickedChar = characterList.get(position);
            EpisodeDetailFragmentDirections.ToCharacterDetailFragmentAction2 action =
                    EpisodeDetailFragmentDirections.toCharacterDetailFragmentAction2();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
