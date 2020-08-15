package com.shevaalex.android.rickmortydatabase.source.network.net_utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class CallAdapterLiveData<T> implements CallAdapter<T, LiveData<ApiResponse<T>>> {

    private Type responseType;

    public CallAdapterLiveData(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public LiveData<ApiResponse<T>> adapt(@NonNull Call<T> call) {
        return new LiveData<ApiResponse<T>>() {
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse<T> apiResponse = new ApiResponse<>();
                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                        postValue(apiResponse.create(response));
                    }

                    @Override
                    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                        postValue(apiResponse.create(t));
                    }
                });
            }
        };
    }
}
