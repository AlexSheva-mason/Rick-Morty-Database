package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;

import java.util.List;

public class EpisodeViewModel extends AndroidViewModel {
    private final MainRepository rmRepository;
    private LiveData<PagedList<Episode>> mEpisodeList;

    public EpisodeViewModel(Application application) {
        super(application);
        rmRepository = MainRepository.getInstance(application);
        mEpisodeList = rmRepository.getAllEpisodes();
    }

    LiveData<PagedList<Episode>> getEpisodeList () {
        if (mEpisodeList == null) {
            mEpisodeList = rmRepository.getAllEpisodes();
        }
        return mEpisodeList;
    }

    LiveData<List<Character>> getCharactersFromEpisode(int id) {
        return  rmRepository.getCharactersFromEpisode(id);
    }

}
