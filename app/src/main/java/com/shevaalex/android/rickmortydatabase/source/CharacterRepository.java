package com.shevaalex.android.rickmortydatabase.source;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.RickMortyDatabase;
import com.shevaalex.android.rickmortydatabase.source.network.ApiCall;
import com.shevaalex.android.rickmortydatabase.source.network.NetworkDataParsing;
import com.shevaalex.android.rickmortydatabase.utils.AppExecutors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CharacterRepository {
    private static final String TAG = "RmRepository";
    private static final Object LOCK = new Object();
    private final NetworkDataParsing networkDataParsing;
    private final RickMortyDatabase rmDatabase;
    private final AppExecutors appExecutors;
    private static CharacterRepository sInstance;
    private Character lastDbCharacter;
    private Character lastNetworkCharacter;
    private boolean dbIsUpToDate;

    private CharacterRepository(Application application) {
        this.networkDataParsing = NetworkDataParsing.getInstance(application);
        this.rmDatabase = RickMortyDatabase.getInstance(application);
        this.appExecutors = AppExecutors.getInstance();
        initialiseDataBase();
    }

    public static synchronized CharacterRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating a new repository instance");
                sInstance = new CharacterRepository(application);
            }
        } else {
            Log.d(TAG, "Getting previous repository object");
        }
        return sInstance;
    }

    public boolean dbIsUpToDate() {
        return dbIsUpToDate;
    }

    // calls a method to check if database sync/initialisation is needed and fetch data if necessary
    private void initialiseDataBase() {
        fetchLastDbCharacter();
        appExecutors.networkIO().execute(() -> networkDataParsing.getVolleyResponce(response -> {
            try {
                JSONObject jsonObject = response.getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_INFO);
                int numberOfPages = jsonObject.getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_PAGES);
                compareLastCharacters(numberOfPages);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES));
    }

    // gets the last row ID from SQLite database
    private void fetchLastDbCharacter() {
        Future<Character> future = appExecutors.diskIO().submit(() -> {
            if (rmDatabase.getCharacterDao().showLastInCharacterList() != null) {
                return rmDatabase.getCharacterDao().showLastInCharacterList();
            } else {
                return null;
            }
        });
        try {
            this.lastDbCharacter = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compareLastCharacters(final int numberOfPages) {
        appExecutors.networkIO().execute(() -> networkDataParsing.getVolleyResponce(response -> {
            //get a JSONArray from the last page and fetch the last Character object
            try {
                JSONArray jsonArray = response.getJSONArray(ApiCall.ApiCallCharacterKeys.CHARACTER_ARRAY);
                int lastArrayObject = jsonArray.length() - 1;
                JSONObject characterJson = jsonArray.getJSONObject(lastArrayObject);
                lastNetworkCharacter = parseJsonObject(characterJson);
                if (lastNetworkCharacter != null) {
                    Log.d(TAG, "Last character's ID on server: " + lastNetworkCharacter.getId());
                    //compare character objects from DataBase and Network, make a DB sync if necessary
                    if (!lastNetworkCharacter.equals(lastDbCharacter)) {
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES + numberOfPages));
    }

    private void getNetworkCharacterList(final int pageNumber) {
        appExecutors.networkIO().execute(() -> networkDataParsing.getVolleyResponce(response -> {
            try {
                JSONArray jsonArray = response.getJSONArray(ApiCall.ApiCallCharacterKeys.CHARACTER_ARRAY);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject characterJson = jsonArray.getJSONObject(i);
                    Character newCharacter = parseJsonObject(characterJson);
                    //add new Character to a database
                    appExecutors.diskIO().execute(() -> rmDatabase.getCharacterDao().insertCharacter(newCharacter));
                    //TODO
                    if (newCharacter != null && newCharacter.equals(lastNetworkCharacter)) {
                        dbIsUpToDate = true;
                        networkDataParsing.cancelVolleyRequests();
                        lastDbCharacter = newCharacter;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, ApiCall.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES + pageNumber));
    }

    public void syncDatabase() {
        if (lastDbCharacter == null || !lastDbCharacter.equals(lastNetworkCharacter)) {
            initialiseDataBase();
        }
        Log.d(TAG, "syncDatabase: lstDBid / lstNetId: " + lastDbCharacter.getId() + " / " + lastNetworkCharacter.getId());
    }

    // calls the appropriate method based on search query and filter applied
    public LiveData<PagedList<Character>> getCharacterListFiltered(String query, int filter) {
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

    private Character parseJsonObject (JSONObject characterJson) {
        try {
            int id = characterJson.getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_ID);
            String name = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_NAME);
            String status = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_STATUS);
            String species = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_SPECIES);
            String type = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_TYPE);
            String gender = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_GENDER);
            JSONObject originLocation = characterJson.getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_ORIGIN_LOCATION);
            String originLocString = originLocation.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_URL);
            int originLocId = 0;
            if (originLocString.contains(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_SUBSTRING)) {
                int stringEnd = originLocString.lastIndexOf(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_SUBSTRING);
                originLocId = Integer.parseInt(originLocString.substring(stringEnd + 9));
            }
            JSONObject lastKnownLoc = characterJson.getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_LAST_LOCATION);
            String lastKnownLocString = lastKnownLoc.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_URL);
            int lastKnownLocId = 0;
            if (lastKnownLocString.contains(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_SUBSTRING)) {
                int stringEnd = lastKnownLocString.lastIndexOf(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_SUBSTRING);
                lastKnownLocId = Integer.parseInt(lastKnownLocString.substring(stringEnd+9));
            }
            String imgUrl = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_IMAGE_URL);
            String episodeList = characterJson.getString(ApiCall.ApiCallCharacterKeys.CHARACTER_EPISODE_LIST);
            return new Character(id, name, status, species, type, gender, originLocId,
                    lastKnownLocId, imgUrl, episodeList);
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }
    }

}