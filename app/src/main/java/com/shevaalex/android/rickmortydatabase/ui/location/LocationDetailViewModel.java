package com.shevaalex.android.rickmortydatabase.ui.location;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Character;

import java.util.List;

public class LocationDetailViewModel extends AndroidViewModel {
    private static final String SAVED_STATE_KEY_LOCATION_ID = "location_id";
    private final MainRepository rmRepository;
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<Integer> locationId;

    public LocationDetailViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        locationId = savedStateHandle.getLiveData(SAVED_STATE_KEY_LOCATION_ID, 0);
        rmRepository = MainRepository.getInstance(application);
    }

    void setLocationId (int locationId) {
        savedStateHandle.set(SAVED_STATE_KEY_LOCATION_ID, locationId);
    }

    LiveData<List<Character>> getCharactersFromLocation() {
        if (locationId.getValue() != null) {
            return rmRepository.getCharactersFromLocation(locationId.getValue());
        }
        return null;
    }
}
