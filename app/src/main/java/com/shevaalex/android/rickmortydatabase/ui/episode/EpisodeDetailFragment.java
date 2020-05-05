package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodeDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;

import java.util.ArrayList;
import java.util.List;

public class EpisodeDetailFragment extends Fragment implements CharacterAuxAdapter.OnCharacterListener {
    private static final String SAVE_STATE_LIST = "List_state";
    private FragmentEpisodeDetailBinding binding;
    private CharacterAuxAdapter characterAuxAdapter;
    private EpisodeViewModel viewModel;
    private Activity a;
    private List<Character> characterList = new ArrayList<>();

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
        if (binding.recyclerviewEpisodeDetail.getAdapter() != null) {
            binding.recyclerviewEpisodeDetail.getAdapter().setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        }
        viewModel.getCharactersFromEpisode(episodeID).observe(getViewLifecycleOwner(), characters -> {
            characterAuxAdapter.setCharacterList(characters);
            characterList = characters;
        });
        if (savedInstanceState != null) {
            Parcelable listState = savedInstanceState.getParcelable(SAVE_STATE_LIST);
            if (binding.recyclerviewEpisodeDetail.getLayoutManager() != null) {
                new Handler().postDelayed(() ->
                        binding.recyclerviewEpisodeDetail.getLayoutManager().onRestoreInstanceState(listState), 50);
            }
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (binding.recyclerviewEpisodeDetail.getLayoutManager() != null) {
            outState.putParcelable(SAVE_STATE_LIST, binding.recyclerviewEpisodeDetail.getLayoutManager().onSaveInstanceState());
        }
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
        if (characterList != null && !characterList.isEmpty()) {
            Character clickedChar = characterList.get(position);
            EpisodeDetailFragmentDirections.ToCharacterDetailFragmentAction2 action =
                    EpisodeDetailFragmentDirections.toCharacterDetailFragmentAction2();
            if (clickedChar != null) {
                action.setCharacterName(clickedChar.getName()).setImageUrl(clickedChar.getImgUrl())
                        .setCharacterStatus(clickedChar.getStatus()).setCharacterSpecies(clickedChar.getSpecies())
                        .setCharacterType(clickedChar.getType()).setCharacterGender(clickedChar.getGender())
                        .setCharacterOrigin(clickedChar.getOriginLocation()).setCharacterLastLocation(clickedChar.getLastKnownLocation())
                        .setId(clickedChar.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
