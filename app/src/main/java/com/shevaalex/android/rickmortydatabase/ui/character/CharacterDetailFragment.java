package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CharacterDetailFragment extends Fragment implements EpisodeAuxAdapter.OnEpisodeListener {
    private static final String BUNDLE_SAVE_STATE_KEY = "list_state";
    private FragmentCharacterDetailBinding binding;
    private CharacterViewModel viewModel;
    private Activity a;
    private EpisodeAuxAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Episode> episodeList = new ArrayList<>();
    private Context context;

    public CharacterDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        //set the recyclerview
        layoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewCharacterDetail.setLayoutManager(layoutManager);
        binding.recyclerviewCharacterDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        adapter = new EpisodeAuxAdapter(this, context);
        binding.recyclerviewCharacterDetail.setAdapter(adapter);
        viewModel.getEpisodeList(characterId).observe(getViewLifecycleOwner(), episodes -> {
            episodeList = episodes;
            Character headerCharacter = viewModel.getCharacterById(characterId);
            setToolbarImage(headerCharacter);
            adapter.setHeaderCharacter(headerCharacter);
            if (headerCharacter.getOriginLocation() != 0) {
                adapter.setOriginLocation(viewModel.getLocationById(headerCharacter.getOriginLocation()));
            }
            if (headerCharacter.getLastKnownLocation() != 0) {
                adapter.setLastLocation(viewModel.getLocationById(headerCharacter.getLastKnownLocation()));
            }
            adapter.setEpisodeList(episodes);
            if (savedInstanceState != null) {
                Parcelable savedState = savedInstanceState.getParcelable(BUNDLE_SAVE_STATE_KEY);
                if (layoutManager != null) {
                    layoutManager.onRestoreInstanceState(savedState);
                }
            }
        });
        return view;
    }

    private void setToolbarImage(Character headerCharacter) {
        //check if app is currently in dark theme and set the contentScrimColor of collapsing toolbar to surface color
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            binding.collapsingToolbarLayout.setContentScrimColor(context.getResources().getColor(R.color.rm_grey_900));
        }
        AppBarLayout appBarLayout = binding.appbarLayout;
        if (headerCharacter != null) {
            int stringLength = headerCharacter.getName().length();
            if (stringLength >= 30) {
                binding.collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.TextAppearance_RM_Toolbar_Collapsed_Title_Small);
            }
            Picasso.get().load(headerCharacter.getImgUrl()).error(R.drawable.picasso_placeholder_error)
                    .fit().centerCrop().into(binding.imageCharacterToolbar, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    appBarLayout.setExpanded(false);
                }
            });
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(view);
        //Set the action bar to show appropriate title, set top level destinations
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.charactersListFragment, R.id.locationsListFragment, R.id.episodesListFragment).build();
        Toolbar toolbar = binding.toolbarFragmentCharacterDetail;
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedState = savedInstanceState.getParcelable(BUNDLE_SAVE_STATE_KEY);
            if (layoutManager != null) {
                layoutManager.onRestoreInstanceState(savedState);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (layoutManager != null) {
            outState.putParcelable(BUNDLE_SAVE_STATE_KEY, layoutManager.onSaveInstanceState());
        }
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
    public void onPause() {
        super.onPause();
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
