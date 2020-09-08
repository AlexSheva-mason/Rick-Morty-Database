package com.shevaalex.android.rickmortydatabase.ui;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.source.MainRepository;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.Resource;
import com.shevaalex.android.rickmortydatabase.utils.StatusMediatorLiveData;
import com.shevaalex.android.rickmortydatabase.utils.networking.ConnectionLiveData;


public class NetworkStatusViewModel extends AndroidViewModel {
    public final MainRepository rmRepository;
    private final StatusMediatorLiveData statusLiveData;

    public NetworkStatusViewModel(@NonNull Application application) {
        super(application);
        rmRepository = MainRepository.getInstance(application);
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(application);
        statusLiveData = new StatusMediatorLiveData(rmRepository.getDatabaseIsUpToDate(), connectionLiveData);
    }

    public LiveData<Pair<Boolean, Boolean>> getNetworkStatusLiveData() {
        return statusLiveData;
    }

    //retrofit test
    public LiveData<Resource<CharacterModel>> dbInitTest() {
        return rmRepository.databaseInit();
    }

    public void checkDatabase() {
        rmRepository.databaseInit();
    }
}
