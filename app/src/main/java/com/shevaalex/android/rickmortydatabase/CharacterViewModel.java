package com.shevaalex.android.rickmortydatabase;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.database.Character;
import com.shevaalex.android.rickmortydatabase.ui.character.FilterLiveData;

public class CharacterViewModel extends AndroidViewModel {
    private RmRepository rmRepository;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<Integer> filterResultKey = new MutableLiveData<>();
    private LiveData<PagedList<Character>> mCharacterList;



    public CharacterViewModel(@NonNull Application application) {
        super(application);
        rmRepository = RmRepository.getInstance(application);
    }

    public void setNameQuery(String name) {
        this.searchQuery.setValue(name);
    }

    public void setFilter(Integer key) {
        this.filterResultKey.setValue(key);
    }

    public LiveData<PagedList<Character>> getCharacterList() {
        if (mCharacterList == null) {
            FilterLiveData trigger = new FilterLiveData(searchQuery, filterResultKey);
            mCharacterList = Transformations.switchMap(trigger,
                    value -> rmRepository.getCharacterListFiltered(value.first, value.second));
        }
        return mCharacterList;
    }

    public boolean dbIsNotSynced() {
        return !rmRepository.dbIsUpToDate();
    }

    public void syncDb() {
        rmRepository.syncDatabase();
    }

    public LiveData<Integer> getFilterResultKey() {
        return filterResultKey;
    }

    public LiveData<String> getSearchQuery() { return searchQuery;   }

}
