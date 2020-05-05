package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shevaalex.android.rickmortydatabase.databinding.FragmentEpisodesListBinding;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;

public class EpisodesListFragment extends Fragment implements EpisodeAdapter.OnEpisodeClickListener {
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        binding.recyclerviewEpisode.setLayoutManager(linearLayoutManager);
        binding.recyclerviewEpisode.setHasFixedSize(true);
        //instantiate an adapter and set this fragment as a listener for onClick
        episodeAdapter = new EpisodeAdapter(this);
        episodeAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.recyclerviewEpisode.setAdapter(episodeAdapter);
        //set the fast scroller for recyclerview
        binding.fastScroll.setRecyclerView(binding.recyclerviewEpisode);
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
