package com.shevaalex.android.rickmortydatabase.source.network;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel;
import com.shevaalex.android.rickmortydatabase.utils.AppExecutors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class CharacterNetworkClient {
    private static final int NETWORK_TIMEOUT = 3000;
    private static final Object LOCK = new Object();
    private static volatile CharacterNetworkClient sCharacterNetworkClient;
    private RetrieveCharacterPageRunnable retrieveCharacterPageRunnable;
    private MutableLiveData<List<CharacterModel>> mCharacterList = new MutableLiveData<>();

    private CharacterNetworkClient() {
        if (sCharacterNetworkClient != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static synchronized CharacterNetworkClient getInstance() {
        if (sCharacterNetworkClient == null) {
            synchronized (LOCK) {
                if (sCharacterNetworkClient == null) {
                    sCharacterNetworkClient = new CharacterNetworkClient();
                }
            }
        }
        return sCharacterNetworkClient;
    }

    public LiveData<List<CharacterModel>> getCharacterList() {
        return mCharacterList;
    }

    public void callCharacterPage(int pageNumber) {
        if (retrieveCharacterPageRunnable != null) {
            retrieveCharacterPageRunnable = null;
        }
        retrieveCharacterPageRunnable = new RetrieveCharacterPageRunnable(pageNumber);
        final Future handler
                = AppExecutors.getInstance().networkIO().submit(retrieveCharacterPageRunnable);
        //set the network timeout limit to 3sec (cancels request after 3sec)
        AppExecutors.getInstance().networkIO().schedule(() -> {
            handler.cancel(true);
        }, NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveCharacterPageRunnable implements Runnable {
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveCharacterPageRunnable(int pageNumber) {
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            if (cancelRequest) {
                return;
            }
            try {
                Response<CharacterPageModel> response = getCharacterPage(pageNumber).execute();
                if (response.code() == 200 && response.body() != null) {
                    List<CharacterModel> characterModelList
                            = new ArrayList<>(response.body().getCharacterModels());
                    if (pageNumber == 1) {
                        mCharacterList.postValue(characterModelList);
                    } else {
                        if (mCharacterList.getValue() != null) {
                            List<CharacterModel> currentList =
                                    new ArrayList<>(mCharacterList.getValue());
                            currentList.addAll(characterModelList);
                            mCharacterList.postValue(currentList);
                        }
                    }
                } else if (response.errorBody() != null) {
                    //TODO
                    Log.e("TAGg", "runnable error: " + response.errorBody().toString());
                    mCharacterList.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mCharacterList.postValue(null);
            }
        }

        private Call<CharacterPageModel> getCharacterPage(int pageNumber) {
            return RetrofitService
                    .getInstance()
                    .getCharacterApi()
                    .getCharactersPage(String.valueOf(pageNumber));
        }

        private void cancelRequest() {
            cancelRequest = true;
        }
    }
}