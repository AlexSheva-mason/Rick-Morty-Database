package com.shevaalex.android.rickmortydatabase.ui.location;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

public class LocationListViewModel extends AndroidViewModel {
    private final MainRepository rmRepository;
    private LiveData<PagedList<Location>> mLocationList;

    public LocationListViewModel(Application application) {
        super(application);
        rmRepository = MainRepository.getInstance(application);
        mLocationList = rmRepository.getAllLocations();
    }

    LiveData<PagedList<Location>> getLocationList() {
        if (mLocationList == null) {
            mLocationList = rmRepository.getAllLocations();
        }
        return mLocationList;
    }
}
