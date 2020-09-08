package com.shevaalex.android.rickmortydatabase.source.network.net_utils;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel;
import com.shevaalex.android.rickmortydatabase.models.ApiPageInfoModel;
import com.shevaalex.android.rickmortydatabase.models.ApiPageModel;
import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;

import java.util.ArrayList;
import java.util.List;

// ObjectModel: Single object type of a given category
// PageModelObject: Type for the API response (API response with category page data)
public abstract class InitManager<ObjectModel extends ApiObjectModel, PageModelObject extends ApiPageModel> {
    private static final String TAG = "LOG_TAG_InitManager";
    //sets the time in days when the refresh is due
    private static final int REFRESH_CONSTANT = 30;
    //result observable object to get status of network and database
    private MediatorLiveData<Resource<ObjectModel>> resultObjectModel = new MediatorLiveData<>();

    public InitManager() {
        init();
    }

    private void init(){
        //set the loading status for livedata
        resultObjectModel.setValue(Resource.loading(null));
        //observe last object livedata from the database
        final LiveData<ObjectModel> dbSourceLastObject = getLastEntryFromDb();
        final LiveData<ApiResponse<PageModelObject>> firstPage = createCall();
        resultObjectModel.addSource(dbSourceLastObject, lastObjectFromDb -> {
            if (lastObjectFromDb != null) {
                Log.d(TAG, "init: last db object ID: " + lastObjectFromDb.getId());
            }
            //call and observe the first api page
            resultObjectModel.addSource(firstPage, pageModelObjectApiResponse -> {
                //if response is successfull -> get the last object's id and fetch it
                if (pageModelObjectApiResponse instanceof ApiResponse.SuccessApiResponse) {
                    ApiPageInfoModel pageInfo =
                            ((ApiResponse.SuccessApiResponse<PageModelObject>) pageModelObjectApiResponse)
                                    .getBody()
                                    .getApiPageInfoModel();
                    int lastModelId = pageInfo.getCount();
                    int pageCount = pageInfo.getPages();
                    LiveData<ApiResponse<ObjectModel>> apiSourcelastObject
                            = callLastApiModel(lastModelId);
                    // if ApiResponse with last object is successfull - set it as Resource.success value
                    resultObjectModel.addSource(apiSourcelastObject, lastNetworkObject -> {
                        Log.d(TAG, "init: success ApiResponse single object");
                        if (lastNetworkObject instanceof ApiResponse.SuccessApiResponse) {
                            resultObjectModel
                                    .setValue(Resource
                                    .success(((ApiResponse.SuccessApiResponse<ObjectModel>) lastNetworkObject)
                                            .getBody()));
                            shouldFetch(((ApiResponse.SuccessApiResponse<ObjectModel>) lastNetworkObject)
                                            .getBody()
                                    , lastObjectFromDb, pageCount);
                        } else {
                            manageEmptyOrErrorResponse(lastNetworkObject);
                        }
                        resultObjectModel.removeSource(apiSourcelastObject);
                    });
                } else {
                    manageEmptyOrErrorResponse(pageModelObjectApiResponse);
                }
                resultObjectModel.removeSource(firstPage);
            });
            resultObjectModel.removeSource(dbSourceLastObject);
        });
    }

    /**
     * Called to decide whether an update of the database is needed
     * 1) Fetch if no cached object found
     * 2) Fetch if objects are different (eg language changed)
     * 3) Fetch if timestamp is older than 30 days
     * 4) Fetch if last network object id > database objects count
     */
    private void shouldFetch(ObjectModel lastNetworkObject,
                             ObjectModel lastCacheObject,
                             int pageCount) {
        final LiveData<Integer> dbObjectsCount = getDbEntriesCount();
        //fetch if no cached object found
        if (lastCacheObject == null) {
            Log.d(TAG, "shouldFetch true: last cache obj is null");
            fetchFromNetwork(pageCount);
            return;
        }
        //fetch if objects are not equal
        if (!areNetworkAndDbEntriesEqual(lastNetworkObject, lastCacheObject)) {
            Log.d(TAG, "shouldFetch true: objects are not equal");
            fetchFromNetwork(pageCount);
            return;
        }
        //fetch if timestamp is older than 30 days
        if (isTimestampObsolete(lastCacheObject)) {
            Log.d(TAG, "shouldFetch true: timestamp is older than refreshConstant");
            fetchFromNetwork(pageCount);
            return;
        }
        //observe number of saved entries in the database
        resultObjectModel.addSource(dbObjectsCount, dbCount -> {
            if (dbCount == null) {
                dbCount = 0;
            }
            Log.d(TAG, "shouldFetch: lastNetworkObject.getId() + dbCount: "
                    + lastNetworkObject.getId() + " + " + dbCount);
            //fetch if last network object id > number of database entries
            if (lastNetworkObject.getId() > dbCount) {
                fetchFromNetwork(pageCount);
            } else {
                Log.d(TAG, "shouldFetch: fetch not needed");
            }
            resultObjectModel.removeSource(dbObjectsCount);
        });
    }

    private void fetchFromNetwork(int pageCount){
        Log.d(TAG, "fetchFromNetwork: getting new data...");
        LiveData<List<ApiResponse<PageModelObject>>> pageObjectsList = callAllPages(pageCount);
        ArrayList<ApiResponse.SuccessApiResponse<PageModelObject>> apiResponsePageList
                = new ArrayList<>();
        resultObjectModel.addSource(pageObjectsList, apiResponseList -> {
            if (apiResponseList != null && !apiResponseList.isEmpty()) {
                Log.d(TAG, "fetchFromNetwork: apiResponseList.size= " + apiResponseList.size());
                for (ApiResponse<PageModelObject> singleApiResponsePage : apiResponseList) {
                    if (singleApiResponsePage instanceof ApiResponse.SuccessApiResponse) {
                        Log.d(TAG, "fetchFromNetwork: singleApiResponsePage= "
                                + ((ApiResponse.SuccessApiResponse<PageModelObject>) singleApiResponsePage).getBody().getApiPageInfoModel().toString());
                        if (!apiResponsePageList.contains(singleApiResponsePage)) {
                            apiResponsePageList
                                    .add((ApiResponse.SuccessApiResponse<PageModelObject>) singleApiResponsePage);
                        }
                    } else {
                        onFetchFailed();
                    }
                }
                Log.d(TAG, "fetchFromNetwork: apiResponsePageList.size= " + apiResponsePageList.size());
                //save result to database when all data is available
                if (apiResponsePageList.size() == pageCount) {
                    saveCallResult(apiResponsePageList);
                    resultObjectModel.removeSource(pageObjectsList);
                }
                if (apiResponseList.get(0) instanceof ApiResponse.EmptyApiResponse
                        || apiResponseList.get(0) instanceof ApiResponse.ErrorApiResponse) {
                    manageEmptyOrErrorResponse(apiResponseList.get(0));
                }
            }
        });
    }

    // if response is empty or error -> set the value as Resource.error
    private <T> void manageEmptyOrErrorResponse(ApiResponse<T> notSuccessfullResponse) {
        if (notSuccessfullResponse instanceof ApiResponse.EmptyApiResponse) {
            Log.d(TAG, "manageEmptyOrErrorResponse: EmptyApiResponse");
            resultObjectModel
                    .setValue(Resource.error("EmptyApiResponse", null));
        } else if (notSuccessfullResponse instanceof ApiResponse.ErrorApiResponse) {
            Log.d(TAG, "manageEmptyOrErrorResponse: ErrorApiResponse");
            resultObjectModel
                    .setValue(Resource
                            .error(((ApiResponse.ErrorApiResponse<T>) notSuccessfullResponse)
                                    .getErrorMessage(), null));
        }
        if (notSuccessfullResponse == null) {
            Log.d(TAG, "manageEmptyOrErrorResponse: EmptyApiResponse");
            resultObjectModel
                    .setValue(Resource.error("null object in response", null));
        }
    }

    //compares network and database objects
    private boolean areNetworkAndDbEntriesEqual(ObjectModel networkObjectModel,
                                                ObjectModel cacheObjectModel) {
        if (cacheObjectModel != null) {
            Log.d(TAG, "areNetworkAndDbEntriesEqual: " + networkObjectModel.equals(cacheObjectModel));
            Log.d(TAG, "areNetworkAndDbEntriesEqual: net/cache= "
                    + networkObjectModel.getId() + " / " + cacheObjectModel.getId());
        }
        return networkObjectModel.equals(cacheObjectModel);
    }

    /**
     * gets current time in days and compares it to a timestamp from the cache object
     * @return true if timestamp is older than REFRESH_CONSTANT
     */
    private boolean isTimestampObsolete(ObjectModel lastCacheObject) {
        int currentTime = (int) (System.currentTimeMillis()/86400000);
        Log.d(TAG, "shouldFetch: currentTime= " + currentTime);
        int lastRefresh = lastCacheObject.getTimeStamp();
        Log.d(TAG, "shouldFetch: lastRefresh= " + lastRefresh);
        Log.d(TAG, "shouldFetch: last refreshed: "
                + (currentTime - lastRefresh)
                + " days ago");
        return (currentTime - lastRefresh) >= REFRESH_CONSTANT;
    }

    // Called when the fetch fails.
    protected void onFetchFailed() {
        Log.d(TAG, "onFetchFailed: fetch failed for at least one ApiResponse<PageModelObject>");
        resultObjectModel
                .setValue(Resource.error("Fetch failed", null));
    }

    @WorkerThread
    protected abstract LiveData<ApiResponse<ObjectModel>> callLastApiModel(int lastModelId);

    //returns last entry from database
    @NonNull @MainThread
    protected abstract LiveData<ObjectModel> getLastEntryFromDb();

    //returns number of entries from database
    @NonNull @MainThread
    protected abstract LiveData<Integer> getDbEntriesCount();

    //calls all pages in sequence and returns a merged result
    @NonNull
    @MainThread
    protected abstract LiveData<List<ApiResponse<PageModelObject>>> callAllPages(int pageCount);

    //calls first page of a given category
    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<PageModelObject>> createCall();

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract void saveCallResult(List<ApiResponse.SuccessApiResponse<PageModelObject>> apiResponseList);

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class. Used as a status object to update the state of the UI (eg loading / sync needed)
    public final LiveData<Resource<ObjectModel>> getAsLiveData() {
        return resultObjectModel;
    }

}
