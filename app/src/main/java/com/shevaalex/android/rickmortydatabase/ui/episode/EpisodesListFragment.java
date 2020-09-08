package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodesListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.ui.FragmentToolbarSimple;
import com.shevaalex.android.rickmortydatabase.utils.CustomItemDecoration;

import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class EpisodesListFragment extends FragmentToolbarSimple
        implements EpisodeAdapter.OnEpisodeClickListener {
    private Activity a;
    private EpisodeListViewModel episodeListViewModel;
    private FragmentEpisodesListBinding binding;
    private EpisodeAdapter episodeAdapter;

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
        episodeListViewModel = new ViewModelProvider
                .AndroidViewModelFactory(a.getApplication()).create(EpisodeListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEpisodesListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setRecyclerView();
        registerObservers();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (episodeAdapter != null) {
            episodeAdapter = null;
        }
        binding = null;
    }

    private void setRecyclerView() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int spanCount = a.getApplicationContext()
                    .getResources()
                    .getInteger(R.integer.grid_span_count);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(a.getApplicationContext(),
                    spanCount,
                    RecyclerView.HORIZONTAL,
                    false);
            binding.recyclerviewEpisode.setLayoutManager(gridLayoutManager);
            // apply spacing to gridlayout
            CustomItemDecoration itemDecoration = new CustomItemDecoration(a, false);
            binding.recyclerviewEpisode.addItemDecoration(itemDecoration);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
            binding.recyclerviewEpisode.setLayoutManager(linearLayoutManager);
            //set fast scroller for API >= 24 (doesn't work on lower APIs)
            if (Build.VERSION.SDK_INT >= 24) {
                Drawable drawable = ContextCompat.getDrawable(a, R.drawable.track_drawable);
                if (drawable != null) {
                    new FastScrollerBuilder(binding.recyclerviewEpisode)
                            .setTrackDrawable(drawable)
                            .build();
                }
            }
        }
        binding.recyclerviewEpisode.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        episodeAdapter = new EpisodeAdapter(requireContext(), this);
        episodeAdapter
                .setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    private void registerObservers() {
        episodeListViewModel.getEpisodeList().observe(getViewLifecycleOwner(), episodes -> {
            episodeAdapter.submitList(episodes);
            binding.recyclerviewEpisode.setAdapter(episodeAdapter);
        } );
    }

    @Override
    public void onEpisodeClick(int position, View v) {
        PagedList<Episode> episodeList = episodeAdapter.getCurrentList();
        if (episodeList != null && !episodeList.isEmpty()) {
            Episode clickedEpisode = episodeList.get(position);
            EpisodesListFragmentDirections.ToEpisodeDetailFragmentAction action =
                    EpisodesListFragmentDirections.toEpisodeDetailFragmentAction();
            if (clickedEpisode != null) {
                action.setEpisodeName(clickedEpisode.getName())
                        .setEpisodeAirDate(clickedEpisode.getAirDate())
                        .setEpisodeCode(clickedEpisode.getCode())
                        .setId(clickedEpisode.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }
}
