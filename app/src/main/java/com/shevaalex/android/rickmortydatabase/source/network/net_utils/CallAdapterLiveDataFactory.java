package com.shevaalex.android.rickmortydatabase.source.network.net_utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class CallAdapterLiveDataFactory extends CallAdapter.Factory {
    /**
     * This method performs a number of checks and then returns the Response type for the Retrofit requests
     * (@bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse)
     *
     * CHECK #1) returnType returns LIVEDATA
     * CHECK #2) Type LiveData<T> is of ApiResponse.class
     * CHECK #3) Make sure ApiResponse is parameeterized. (ApiResponse<T> exists)
     */
    @Override
    public CallAdapter<?, ?> get(@NonNull Type returnType,
                                 @NonNull Annotation[] annotations,
                                 @NonNull Retrofit retrofit) {
        // Check #1
        // Make sure the CallAdapter is returning a type of LiveData
        if(CallAdapter.Factory.getRawType(returnType) != LiveData.class){
            return null;
        }

        // Check #2
        // Check what type LiveData is wrapped around
        Type observableType =
                CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);
        // Check if it's of Type ApiResponse
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if(rawObservableType != ApiResponse.class){
            throw new IllegalArgumentException("type must be of a defined resource");
        }

        // Check #3
        // Check if ApiResponse is parameterized. (ApiResponse<T> exists)
        if(!(observableType instanceof ParameterizedType)){
            throw new IllegalArgumentException("resource must be parameterized");
        }

        // get the Response type
        Type bodyType =
                CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);
        return new CallAdapterLiveData<Type>(bodyType);
    }
}
