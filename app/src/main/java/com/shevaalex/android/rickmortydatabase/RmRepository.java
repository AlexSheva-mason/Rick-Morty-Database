package com.shevaalex.android.rickmortydatabase;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.database.Character;
import com.shevaalex.android.rickmortydatabase.database.RickMortyDatabase;
import com.shevaalex.android.rickmortydatabase.networking.ApiCall;
import com.shevaalex.android.rickmortydatabase.networking.NetworkDataParsing;
import com.shevaalex.android.rickmortydatabase.utils.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class RmRepository {
    private static final String TAG = "RmRepository";
    private static final Object LOCK = new Object();
    private final NetworkDataParsing networkDataParsing;
    private final RickMortyDatabase rmDatabase;
    private final AppExecutors appExecutors;
    private static RmRepository sInstance;
    private int lastDbCharacterId;
    private int lastNetworkCharacterId;
    private boolean dbIsUpToDate;

    private RmRepository(Application application) {
        this.networkDataParsing = NetworkDataParsing.getInstance(application);
        this.rmDatabase = RickMortyDatabase.getInstance(application);
        this.appExecutors = AppExecutors.getInstance();
        initialiseDataBase();
    }

    static synchronized RmRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating a new repository instance");
                sInstance = new RmRepository(application);
            }
        } else {
            Log.d(TAG, "Getting previous repository object");
        }
        return sInstance;
    }

    boolean dbIsUpToDate() {
        return dbIsUpToDate;
    }

    // TODO add progressbar when loading data
    // calls a method to check if database sync/initialisation is needed and fetch data if necessary
    private void initialiseDataBase() {
        fetchLastDbCharacterID();
        appExecutors.networkIO().execute(() -> networkDataParsing.getCharactersNumberOfPages(response -> {
            try {
                JSONObject jsonObject = response.getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_INFO);
                int numberOfPages = jsonObject.getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_PAGES);
                // for testing
                Log.d(TAG, "Number of pages in Characters: " + numberOfPages);
                compareNetworkCharacterID(numberOfPages);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES));
    }

    // gets the last row ID from SQLite database
    private void fetchLastDbCharacterID() {
        Future<Integer> future = appExecutors.diskIO().submit(() -> {
            if (rmDatabase.getCharacterDao().showLastInCharacterList() != null) {
                return rmDatabase.getCharacterDao().showLastInCharacterList().getId();
            } else {
                return 0;
            }
        });
        try {
            int lastCharId = future.get();
            Log.d(TAG, "Setting LastDbCharacterId: " + lastCharId);
            lastDbCharacterId = lastCharId;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compareNetworkCharacterID(final int numberOfPages) {
        appExecutors.networkIO().execute(() -> networkDataParsing.compareJsonLastCharacterId(response -> {
            //get a JSONArray from the last page and fetch the last character object's ID
            try {
                JSONArray jsonArray = response.getJSONArray(ApiCall.ApiCallCharacterKeys.CHARACTER_ARRAY);
                int lastArrayObject = jsonArray.length() - 1;
                JSONObject characterJson = jsonArray.getJSONObject(lastArrayObject);
                int id = characterJson.getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_ID);
                lastNetworkCharacterId = id;
                Log.d(TAG, "Last character's ID on server: " + id);
                //compare Id's from DataBase and Network, make a DB sync if necessary
                if (id != lastDbCharacterId) {
                    dbIsUpToDate = false;
                    Log.d(TAG, "Database update needed");
                    // loops through all pages and uses data to populate ArrayList
                    for (int x = 1; x < numberOfPages + 1; x++) {
                        getNetworkCharacterList(x);
                    }
                } else {
                    dbIsUpToDate = true;
                    networkDataParsing.cancelVolleyRequests();
                    Log.d(TAG, "Database is up to date, cancelling requests");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES + numberOfPages));
    }

    private void getNetworkCharacterList(final int pageNumber) {
        appExecutors.networkIO().execute(() -> networkDataParsing.getJsonCharacterList(response -> {
            try {
                JSONArray jsonArray = response.getJSONArray(ApiCall.ApiCallCharacterKeys.CHARACTER_ARRAY);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject characterJson = jsonArray.getJSONObject(i);
                    int id = characterJson.getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_ID);
                    lastDbCharacterId = id;
                    String name = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_NAME);
                    String status = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_STATUS);
                    String species = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_SPECIES);
                    String type = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_TYPE);
                    String gender = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_GENDER);
                    // TODO can be unknown
                    String originLocation = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_ORIGIN_LOCATION);
                    // TODO can be unknown
                    String lastKnownLocation = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_LAST_LOCATION);
                    String imgUrl = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_IMAGE_URL);
                    String episodeList = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_EPISODE_LIST);
                    final Character newCharacter = new Character(id, name, status, species, type, gender, originLocation,
                            lastKnownLocation, imgUrl, episodeList);
                    //add new Character to a database
                    appExecutors.diskIO().execute(() -> rmDatabase.getCharacterDao().insertCharacter(newCharacter));
                    if (id == lastNetworkCharacterId) {
                        dbIsUpToDate = true;
                        networkDataParsing.cancelVolleyRequests();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES + pageNumber));
    }

    void syncDatabase() {
        if (lastDbCharacterId == 0 || lastDbCharacterId < lastNetworkCharacterId) {
            initialiseDataBase();
        }
        Log.d(TAG, "syncDatabase: lstDBid / lstNetId: " + lastDbCharacterId + " / " + lastNetworkCharacterId);
    }

    // calls the appropriate method based on search query and filter applied
    LiveData<PagedList<Character>> getCharacterListFiltered(String query, int filter) {
        LiveData<PagedList<Character>> mCharacterList = new LiveData<PagedList<Character>>() {
        };
        if (query == null || query.equals("")) {
            if (filter == 0) {
                mCharacterList = getAllCharacters();
            } else if (filter == 101) {
                mCharacterList = getAllCharsNoDead();
            }
        } else {
            if (filter == 0) {
                mCharacterList = searchInCharacters(query);
            } else if (filter == 101) {
                mCharacterList = searchInCharactersNoDead(query);
            }
        }
        return mCharacterList;
    }

    //gets all characters
    private LiveData<PagedList<Character>> getAllCharacters() {
        return new LivePagedListBuilder<>(rmDatabase.getCharacterDao().showAllCharacters(), 50).setFetchExecutor(appExecutors.diskIO()).build();
    }

    //gets all characters, excludes Dead
    private LiveData<PagedList<Character>> getAllCharsNoDead() {
        return new LivePagedListBuilder<>(rmDatabase.getCharacterDao().showAllCharsNoDead(), 50).setFetchExecutor(appExecutors.diskIO()).build();
    }

    //performs search by name in database, shows all
    private LiveData<PagedList<Character>> searchInCharacters(String query) {
        return new LivePagedListBuilder<>(rmDatabase.getCharacterDao().searchInCharacterList("%" + query + "%"), 50).setFetchExecutor(appExecutors.diskIO()).build();
    }

    //performs search by name in database, excludes Dead
    private LiveData<PagedList<Character>> searchInCharactersNoDead(String query) {
        return new LivePagedListBuilder<>(rmDatabase.getCharacterDao().searchInCharacterListNoDead("%" + query + "%"), 50).setFetchExecutor(appExecutors.diskIO()).build();
    }

}