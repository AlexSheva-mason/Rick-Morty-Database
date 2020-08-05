package com.shevaalex.android.rickmortydatabase.source.network;

import com.shevaalex.android.rickmortydatabase.source.network.requests.CharacterApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final Object LOCK = new Object();
    private static volatile RetrofitService sInstance;
    private CharacterApi characterApi;

    private RetrofitService() {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(ApiConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        characterApi = retrofit.create(CharacterApi.class);
    }

    public static synchronized RetrofitService getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new RetrofitService();
                }
            }
        }
        return sInstance;
    }

    public CharacterApi getCharacterApi() {
        return characterApi;
    }
}
