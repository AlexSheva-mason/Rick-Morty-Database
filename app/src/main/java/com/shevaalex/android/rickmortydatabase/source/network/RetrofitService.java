package com.shevaalex.android.rickmortydatabase.source.network;

import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.CallAdapterLiveDataFactory;
import com.shevaalex.android.rickmortydatabase.source.network.requests.CharacterApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final Object LOCK = new Object();
    private static volatile RetrofitService sRetrofitService;
    private CharacterApi characterApi;
    private static final int CONNECTION_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 5;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build();

    private RetrofitService() {
        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(ApiConstants.BASE_URL)
                        .client(okHttpClient)
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
