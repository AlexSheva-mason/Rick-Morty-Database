package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentCharacterDetailBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.utils.CustomItemDecoration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CharacterDetailFragment extends Fragment implements CharacterDetailAdapter.OnEpisodeListener, View.OnClickListener{
    private FragmentCharacterDetailBinding binding;
    private CharacterViewModel viewModel;
    private Activity a;
    private CharacterDetailAdapter adapter;
    private List<Episode> episodeList = new ArrayList<>();
    private Character headerCharacter;
    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(a);
        viewModel = new ViewModelProvider
                .AndroidViewModelFactory(a.getApplication()).create(CharacterViewModel.class);
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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int spanCount = a.getApplicationContext().getResources().getInteger(R.integer.grid_span_count);
            GridLayoutManager gridLayoutManager =
                    new GridLayoutManager(a.getApplicationContext(), spanCount);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == 0 ? spanCount : 1;
                }
            });
            binding.recyclerviewCharacterDetail.setLayoutManager(gridLayoutManager);
            // apply spacing to gridlayout
            CustomItemDecoration itemDecoration = new CustomItemDecoration(a, true);
            binding.recyclerviewCharacterDetail.addItemDecoration(itemDecoration);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
            binding.recyclerviewCharacterDetail.setLayoutManager(layoutManager);
        }
        binding.recyclerviewCharacterDetail.setHasFixedSize(true);
        //get recyclerview Adapter and set data to it using ViewModel
        adapter = new CharacterDetailAdapter(this, this, context);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewCharacterDetail.setAdapter(adapter);
        viewModel.getEpisodeList(characterId).observe(getViewLifecycleOwner(), episodes -> {
            episodeList = episodes;
            headerCharacter = viewModel.getCharacterById(characterId);
            if (headerCharacter != null) {
                if (binding.collapsingToolbarLayout != null) {
                    binding.collapsingToolbarLayout.setOnClickListener(this);
                    setToolbar(headerCharacter);
                    if (binding.imageCharacterToolbar != null) {
                        setCharacterImage(headerCharacter);
                    }
                }
                adapter.setHeaderCharacter(headerCharacter);
                if (headerCharacter.getOriginLocation() != 0) {
                    adapter.setOriginLocation(viewModel.getLocationById(headerCharacter.getOriginLocation()));
                }
                if (headerCharacter.getLastKnownLocation() != 0) {
                    adapter.setLastLocation(viewModel.getLocationById(headerCharacter.getLastKnownLocation()));
                }
            }
            adapter.setEpisodeList(episodes);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE
                && binding.toolbarFragmentCharacterDetail != null) {
            NavController navController = Navigation.findNavController(view);
            AppBarConfiguration appBarConfiguration =
                    new AppBarConfiguration.Builder(R.id.charactersListFragment,
                            R.id.locationsListFragment,
                            R.id.episodesListFragment)
                            .build();
            NavigationUI.setupWithNavController(
                    binding.toolbarFragmentCharacterDetail, navController, appBarConfiguration);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAnalytics.setCurrentScreen(
                requireActivity(),
                this.getClass().getSimpleName(),
                this.getClass().getSimpleName());
    }

    //set the toolbar and it's title

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        binding = null;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        a = null;
    }

    private void setCharacterImage(Character headerCharacter) {
        Picasso.get()
                .load(headerCharacter.getImgUrl())
                .error(R.drawable.picasso_placeholder_error)
                .into(binding.imageCharacterToolbar);
    }

    private void setToolbar(Character headerCharacter) {
        //check if app is currently in dark theme and set the contentScrimColor of collapsing toolbar to surface color
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES && binding.collapsingToolbarLayout != null) {
            binding.collapsingToolbarLayout.setContentScrimColor(context.getResources().getColor(R.color.rm_grey_900));
        }
        //set expanded title
        if (binding.toolbarTitle != null) {
            binding.toolbarTitle.setVisibility(View.GONE);
            binding.toolbarTitle.setText(headerCharacter.getName());
        }
        // manage custom collapsed/expanded title state and icon
        if (binding.appbarLayout != null
                && binding.collapsingToolbarLayout!= null
                && binding.toolbarFragmentCharacterDetail != null) {
            binding.appbarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> appBarLayout.post(() -> {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {
                    //  Collapsed
                    if (binding.toolbarTitle.getVisibility() == View.VISIBLE) {
                        binding.toolbarTitle.setVisibility(View.GONE);
                    }
                    binding.toolbarFragmentCharacterDetail.setNavigationIcon(a.getDrawable(R.drawable.ic_baseline_arrow_back));
                    binding.collapsingToolbarLayout.setTitleEnabled(true);
                } else if (verticalOffset == 0) {
                    // Fully expanded
                    binding.toolbarFragmentCharacterDetail.setTitle(null);
                    binding.collapsingToolbarLayout.setTitleEnabled(false);
                    binding.toolbarTitle.setVisibility(View.VISIBLE);
                    binding.toolbarFragmentCharacterDetail.setNavigationIcon(a.getDrawable(R.drawable.ic_back_arrw));
                } else {
                    // Not fully expanded not collapsed
                    if (binding.toolbarTitle.getVisibility() == View.VISIBLE) {
                        new Handler().postDelayed(()->binding.toolbarTitle.setVisibility(View.GONE),250);
                    }
                    binding.toolbarFragmentCharacterDetail.setNavigationIcon(a.getDrawable(R.drawable.ic_back_arrw));
                    binding.toolbarFragmentCharacterDetail.setTitle(null);
                    binding.collapsingToolbarLayout.setTitleEnabled(false);
                }
            }));
        }
    }

    public void openImageFragment(View v) {
        if (headerCharacter != null) {
            CharacterDetailFragmentDirections.ToCharacterImageFragment action =
                    CharacterDetailFragmentDirections.toCharacterImageFragment();
            action.setCharacterImageUrl(headerCharacter.getImgUrl())
                    .setCharacterName(headerCharacter.getName());
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

    @Override
    public void onClick(View v) {
        openImageFragment(v);
    }
}
