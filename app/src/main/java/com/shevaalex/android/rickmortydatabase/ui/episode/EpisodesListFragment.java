package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class EpisodesListFragment extends FragmentToolbarSimple implements EpisodeAdapter.OnEpisodeClickListener {
    private Activity a;
    private EpisodeViewModel episodeViewModel;
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
        episodeViewModel = new ViewModelProvider.AndroidViewModelFactory(a.getApplication()).create(EpisodeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEpisodesListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int spanCount = a.getApplicationContext().getResources().getInteger(R.integer.grid_span_count);
            GridLayoutManager gridLayoutManager =
                    new GridLayoutManager(a.getApplicationContext(), spanCount, RecyclerView.HORIZONTAL, false);
            binding.recyclerviewEpisode.setLayoutManager(gridLayoutManager);
            // apply spacing to gridlayout
            CustomItemDecoration itemDecoration = new CustomItemDecoration(a, false);
            binding.recyclerviewEpisode.addItemDecoration(itemDecoration);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
            binding.recyclerviewEpisode.setLayoutManager(linearLayoutManager);
            //set fast scroller for API >= 24 (doesn't work on lower APIs)
            if (Build.VERSION.SDK_INT >= 24) {
                new FastScrollerBuilder(binding.recyclerviewEpisode)
                        .setTrackDrawable(a.getResources().getDrawable(R.drawable.track_drawable, a.getTheme()))
                        .build();
            }
        }
        binding.recyclerviewEpisode.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        episodeAdapter = new EpisodeAdapter(this, requireContext());
        episodeAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewEpisode.setAdapter(episodeAdapter);
        episodeViewModel.getEpisodeList().observe(getViewLifecycleOwner(), episodes -> episodeAdapter.submitList(episodes) );
        return view;
    }

    @Override
    public void onEpisodeClick(int position, View v) {
        PagedList<Episode> episodeList = episodeAdapter.getCurrentList();
        if (episodeList != null && !episodeList.isEmpty()) {
            Episode clickedEpisode = episodeList.get(position);
            EpisodesListFragmentDirections.ToEpisodeDetailFragmentAction action =
                    EpisodesListFragmentDirections.toEpisodeDetailFragmentAction();
            if (clickedEpisode != null) {
                action.setEpisodeName(clickedEpisode.getName()).setEpisodeAirDate(clickedEpisode.getAirDate())
                        .setEpisodeCode(clickedEpisode.getCode()).setId(clickedEpisode.getId());
                Navigation.findNavController(v).navigate(action);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (episodeAdapter != null) {
            episodeAdapter = null;
        }
        binding = null;
    }
}
