package com.shevaalex.android.rickmortydatabase.ui.character;

import android.app.Application;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

import java.util.List;


public class CharacterViewModel extends AndroidViewModel {
    private static final String SAVED_STATE_KEY_QUERY = "query";
    private static final String SAVED_STATE_KEY_FILTER = "filter";
    private static final String SAVED_STATE_KEY_LIST_POSITION = "list_position";
    private static final int KEY_SHOW_ALL = 0;
    public final MainRepository rmRepository;
    private MutableLiveData<String> searchQuery;
    private MutableLiveData<Integer> filterResultKey;
    private MutableLiveData<Parcelable> rvListPosition;
    private LiveData<PagedList<CharacterSmall>> mCharacterList;
    private final FilterLiveData trigger;
    private SavedStateHandle savedStateHandle;

    public CharacterViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;
        searchQuery = savedStateHandle.getLiveData(SAVED_STATE_KEY_QUERY, null);
        filterResultKey = savedStateHandle.getLiveData(SAVED_STATE_KEY_FILTER, KEY_SHOW_ALL);
        rvListPosition = savedStateHandle.getLiveData(SAVED_STATE_KEY_LIST_POSITION);
        rmRepository = MainRepository.getInstance(application);
        trigger = new FilterLiveData(searchQuery, filterResultKey);
    }

    void setNameQuery(String name) {
        //set value + save query to SavedStateHandle
        savedStateHandle.set(SAVED_STATE_KEY_QUERY, name);
    }

    void setFilter(Integer filter) {
        //set value + save filter to SavedStateHandle
        savedStateHandle.set(SAVED_STATE_KEY_FILTER, filter);
    }

    void setListPosition(Parcelable parcelable) {
        //set value + save list position to SavedStateHandle
        savedStateHandle.set(SAVED_STATE_KEY_LIST_POSITION, parcelable);
    }

    LiveData<PagedList<CharacterSmall>> getCharacterList() {
        if (mCharacterList == null) {
            mCharacterList = Transformations.switchMap(trigger,
                    value -> rmRepository.getCharacterListFiltered(value.first, value.second));
        }
        return mCharacterList;
    }

    LiveData<Integer> getFilterResultKey() {
        return filterResultKey;
    }

    LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    LiveData<Parcelable> getListPosition() {
        return rvListPosition;
    }

    Location getLocationById (int id) {
        return rmRepository.getLocationById(id);
    }

    Character getCharacterById (int id) {
        return rmRepository.getCharacterById(id);
    }

    LiveData<List<Episode>> getEpisodeList(int characterId) {
        return rmRepository.getEpisodesFromCharacter(characterId);
    }

}
