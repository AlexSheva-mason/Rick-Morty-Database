package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CharacterDetailFragment extends Fragment implements View.OnClickListener, EpisodeAuxAdapter.OnEpisodeListener {
    private static final String SAVE_STATE_LIST = "List_state";
    private FragmentCharacterDetailBinding binding;
    private CharacterViewModel viewModel;
    private Activity a;
    private Location originLocation;
    private Location lastLocation;
    private EpisodeAuxAdapter adapter;
    private List<Episode> episodeList = new ArrayList<>();
    private static Bundle savedState;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Set View binding for this fragment
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // retrieve data from parent fragment
        int characterId = CharacterDetailFragmentArgs.fromBundle(requireArguments()).getId();
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
        //set views
        setViews(imgUrl, charStatus, charSpecies, charType, charGender, charOriginID, charLastLocID);
        //set the recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewCharacterDetail.setLayoutManager(layoutManager);
        binding.recyclerviewCharacterDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        adapter = new EpisodeAuxAdapter(this);
        binding.recyclerviewCharacterDetail.setAdapter(adapter);
        viewModel.getEpisodeList(characterId).observe(getViewLifecycleOwner(), episodes -> {
            adapter.setEpisodeList(episodes);
            episodeList = episodes;
        });
        return view;
    }

    //sets retreived data to appropriate views
    private void setViews(String imgUrl, String charStatus, String charSpecies, String charType, String charGender, int charOriginID, int charLastLocID) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedState != null) {
            Parcelable listState = savedState.getParcelable(SAVE_STATE_LIST);
            if (binding.recyclerviewCharacterDetail.getLayoutManager() != null) {
                new Handler().postDelayed(() ->
                        binding.recyclerviewCharacterDetail.getLayoutManager().onRestoreInstanceState(listState), 50);
            }
        }
    }

    private void customSaveState() {
        savedState = new Bundle();
        if (binding.recyclerviewCharacterDetail.getLayoutManager() != null) {
            savedState.putParcelable(SAVE_STATE_LIST, binding.recyclerviewCharacterDetail.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        customSaveState();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (adapter != null) {
            adapter = null;
        }
    }

    @Override
    public void onClick(View v) {
        Location clickedLocation = null;
        if (v.getId() == binding.buttonLastLocation.getId()) {
            clickedLocation = lastLocation;
        } else if (v.getId() == binding.buttonOriginLocation.getId()) {
            clickedLocation = originLocation;
        }
        CharacterDetailFragmentDirections.ToLocationDetailFragmentAction2 action =
                CharacterDetailFragmentDirections.toLocationDetailFragmentAction2();
        if (clickedLocation != null) {
            action.setLocationName(clickedLocation.getName()).setLocationDimension(clickedLocation.getDimension())
                    .setLocationType(clickedLocation.getType()).setLocationResidents(clickedLocation.getResidentsList());
            Navigation.findNavController(v).navigate(action);
        }
    }

    @Override
    public void onEpisodeClick(int position, View v) {
            Episode clickedEpisode = episodeList.get(position);
            if (clickedEpisode != null) {
                CharacterDetailFragmentDirections.ToEpisodeDetailFragmentAction2 action =
                        CharacterDetailFragmentDirections.toEpisodeDetailFragmentAction2();
                action.setEpisodeName(clickedEpisode.getName()).setEpisodeAirDate(clickedEpisode.getAirDate())
                        .setEpisodeCode(clickedEpisode.getCode()).setId(clickedEpisode.getId());
                Navigation.findNavController(v).navigate(action);
            }
    }
}
