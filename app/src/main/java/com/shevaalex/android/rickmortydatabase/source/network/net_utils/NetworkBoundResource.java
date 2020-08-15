package com.shevaalex.android.rickmortydatabase.source.network.net_utils;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;
import com.shevaalex.android.rickmortydatabase.utils.AppExecutors;


// CacheObject: Type for the Resource data. (database cache)
// NetworkObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, NetworkObject> {

    private AppExecutors appExecutors;

    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){
        //set the loading status for livedata
        results.setValue(Resource.loading(null));
        //observe livedata from database
        final LiveData<CacheObject> dbSource = loadFromDb();
        results.addSource(dbSource, cacheObject -> {
            results.removeSource(dbSource);
            if (shouldFetch(cacheObject)) {
                //get data from the network
                fetchFromNetwork(dbSource);
            } else {
                //return and observe data from cache
                results.addSource(dbSource, cacheObject1 ->
                        setValue(Resource.success(cacheObject1)));
            }
        });
    }

    private void setValue(Resource<CacheObject> newCacheObject) {
        if (newCacheObject != results.getValue()) {
            results.setValue(newCacheObject);
        }
    }

    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into the database
     * 5) observe the database again to see refreshed data
     * @param dbSource = data from the local db
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource) {
        /*TODO
        1) Fetch if timestamp is older than 30 days(?)
        2) fetch if last network object id > last database object id
        3) Fetch if objects are different eg language changed
        */
        //set the loading status for livedata
        results.addSource(dbSource, cacheObject -> setValue(Resource.loading(cacheObject)));
        final LiveData<ApiResponse<NetworkObject>> apiResponse = createCall();
        results.addSource(apiResponse, networkObjectApiResponse -> {
            results.removeSource(dbSource);
            results.removeSource(apiResponse);
            /*cases:
                1) successfull response
                2) empty response
                3) error
             */
            if (networkObjectApiResponse instanceof ApiResponse.SuccessApiResponse) {
                Log.d("TAGg", "fetchFromNetwork: " + "SuccessApiResponse");
                appExecutors.diskIO().execute(() -> {
                    //save the response to the db
                    saveCallResult(processResponce(
                            (ApiResponse.SuccessApiResponse<NetworkObject>) networkObjectApiResponse));
                    appExecutors.mainThreadExecutor().execute(() ->
                            results.addSource(loadFromDb(), cacheObject ->
                                    setValue(Resource.success(cacheObject))));
                });
            } else if (networkObjectApiResponse instanceof ApiResponse.EmptyApiResponse) {
                Log.d("TAGg", "fetchFromNetwork: " + "EmptyApiResponse");
                appExecutors.mainThreadExecutor().execute(() -> results.addSource(loadFromDb(),
                        cacheObject -> setValue(Resource.success(cacheObject))));
            } else if (networkObjectApiResponse instanceof ApiResponse.ErrorApiResponse) {
                Log.d("TAGg", "fetchFromNetwork: " + "ErrorApiResponse");
                results.addSource(dbSource, cacheObject -> setValue(
                        Resource.error(
                                ((ApiResponse.ErrorApiResponse<NetworkObject>) networkObjectApiResponse)
                                        .getErrorMessage(),
                                cacheObject)
                ));
            }
        });
    }

    private NetworkObject processResponce(
            ApiResponse.SuccessApiResponse<NetworkObject> successApiResponse) {
            return successApiResponse.getBody();
    }

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(@NonNull NetworkObject networkObject);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject cacheObject);

    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<NetworkObject>> createCall();

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    protected void onFetchFailed() {}

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData() {
        return results;
    }
}
