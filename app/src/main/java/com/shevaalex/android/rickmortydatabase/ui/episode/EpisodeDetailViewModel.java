package com.shevaalex.android.rickmortydatabase.ui.episode;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Character;

import java.util.List;

public class EpisodeDetailViewModel extends AndroidViewModel {
    private static final String SAVED_STATE_KEY_EPISODE_ID = "episode_id";
    private final MainRepository rmRepository;
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<Integer> episodeId;

    public EpisodeDetailViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        episodeId = savedStateHandle.getLiveData(SAVED_STATE_KEY_EPISODE_ID, 0);
        rmRepository = MainRepository.getInstance(application);
    }

    void setEpisodeId (int episodeId) {
        savedStateHandle.set(SAVED_STATE_KEY_EPISODE_ID, episodeId);
    }

    //TODO
    //this is not right according to documentation
    //livedata is being reassigned
    //should use a transformation
    LiveData<List<Character>> getCharactersFromEpisode() {
        if (episodeId.getValue() != null) {
            //return  rmRepository.getCharactersFromEpisode(episodeId.getValue());
        }
        return null;
    }

}
