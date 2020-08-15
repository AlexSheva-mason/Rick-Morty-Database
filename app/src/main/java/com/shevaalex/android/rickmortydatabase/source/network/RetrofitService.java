package com.shevaalex.android.rickmortydatabase.source.network;

import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.CallAdapterLiveDataFactory;
import com.shevaalex.android.rickmortydatabase.source.network.requests.CharacterApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final Object LOCK = new Object();
    private static volatile RetrofitService sRetrofitService;
    private CharacterApi characterApi;

    private RetrofitService() {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(ApiConstants.BASE_URL)
                        .addCallAdapterFactory(new CallAdapterLiveDataFactory())
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        characterApi = retrofit.create(CharacterApi.class);
    }

    public static synchronized RetrofitService getInstance() {
        if (sRetrofitService == null) {
            synchronized (LOCK) {
                if (sRetrofitService == null) {
                    sRetrofitService = new RetrofitService();
                }
            }
        }
        return sRetrofitService;
    }

    public CharacterApi getCharacterApi() {
        return characterApi;
    }
}
