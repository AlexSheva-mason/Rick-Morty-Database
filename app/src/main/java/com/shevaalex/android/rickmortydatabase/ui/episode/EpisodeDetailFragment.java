package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shevaalex.android.rickmortydatabase.R;

/**
 * A simple {@link Fragment} subclass.
 */

@SuppressWarnings("WeakerAccess")
public class EpisodeDetailFragment extends Fragment {

    public EpisodeDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_episode_detail, container, false);
    }
}
