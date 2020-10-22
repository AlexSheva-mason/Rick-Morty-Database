package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

import java.util.List;


public class CharacterDetailViewModel extends AndroidViewModel {
    private static final String SAVED_STATE_KEY_CHARECTER_ID = "character_id";
    public final MainRepository rmRepository;
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<Integer> characterId;

    public CharacterDetailViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        characterId = savedStateHandle.getLiveData(SAVED_STATE_KEY_CHARECTER_ID, 0);
        rmRepository = MainRepository.getInstance(application);
    }

    void setCharacterId (int characterId) {
        savedStateHandle.set(SAVED_STATE_KEY_CHARECTER_ID, characterId);
    }

    //TODO
    //this is not right according to documentation
    //livedata is being reassigned
    //should use a transformation
    LiveData<Character> getCharacter() {
        if (characterId.getValue() != null) {
            return rmRepository.getCharacterById(characterId.getValue());
        }
        return null;
    }

    //TODO
    //this is not right according to documentation
    //livedata is being reassigned
    //should use a transformation
    LiveData<List<Episode>> getEpisodeList() {
        if (characterId.getValue() != null) {
            //return rmRepository.getEpisodesFromCharacter(characterId.getValue());
        }
        return null;
    }

    Location getLocationById(int id){
        return rmRepository.getLocationById(id);
    }

}
