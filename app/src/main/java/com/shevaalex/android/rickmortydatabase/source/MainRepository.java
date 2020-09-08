package com.shevaalex.android.rickmortydatabase.source;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.source.database.RickMortyDatabase;
import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;
import com.shevaalex.android.rickmortydatabase.source.network.RetrofitService;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;
import com.shevaalex.android.rickmortydatabase.source.network.NetworkDataParsing;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.InitManager;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.Resource;
import com.shevaalex.android.rickmortydatabase.ui.MainActivity;
import com.shevaalex.android.rickmortydatabase.utils.AppExecutors;
import com.shevaalex.android.rickmortydatabase.utils.RepoHelperUtil;
import com.shevaalex.android.rickmortydatabase.utils.UiTranslateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainRepository {
    private static final String TAG = "LOG_TAG_MainRepository";
    private static final Object LOCK = new Object();
    private final NetworkDataParsing networkDataParsing;
    private final RickMortyDatabase rmDatabase;
    private final AppExecutors appExecutors;
    private static volatile MainRepository sMainRepository;
    private Character lastDbCharacter;
    private Character lastNetworkCharacter;
    private Location lastDbLocation;
    private Location lastNetworkLocation;
    private Episode lastDbEpisode;
    private Episode lastNetworkEpisode;
    private int characterEntriesDbCount;
    private int locationEntriesDbCount;
    private int episodeEntriesDbCount;
    private boolean characterTableIsUpToDate;
    private boolean locationTableIsUpToDate;
    private boolean episodeTableIsUpToDate;
    private ArrayList<Character> mCharacterList;
    private ArrayList<Location> mLocationList;
    private ArrayList<Episode> mEpisodeList;
    private Context mContext;
    //set LiveData to monitor database sync status via ViewModel
    private MutableLiveData<Boolean> dbIsUpToDate;

    private MainRepository(Application application) {
        if (sMainRepository != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.networkDataParsing = NetworkDataParsing.getInstance(application);
        this.rmDatabase = RickMortyDatabase.getInstance(application);
        this.appExecutors = AppExecutors.getInstance();
        initialiseDataBase();
        mContext = application.getApplicationContext();
    }

    public static synchronized MainRepository getInstance(Application application) {
        if (sMainRepository == null) {
            synchronized (LOCK) {
                if (sMainRepository == null) {
                    sMainRepository = new MainRepository(application);
                }
            }
        }
        return sMainRepository;
    }

    public LiveData<Boolean> getDatabaseIsUpToDate() {
        if (dbIsUpToDate == null) {
            dbIsUpToDate = new MutableLiveData<>();
            dbIsUpToDate.setValue(false);
        }
        return dbIsUpToDate;
    }

    // calls a method to check if database sync/initialisation is needed and fetch data if necessary
    public void initialiseDataBase() {
        mCharacterList = new ArrayList<>();
        mLocationList = new ArrayList<>();
        mEpisodeList = new ArrayList<>();
        characterTableIsUpToDate = false;
        locationTableIsUpToDate = false;
        episodeTableIsUpToDate = false;
        fetchLastDbEntries();
        String [] baseUrlArray = {ApiConstants.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES,
                ApiConstants.ApiCallLocationKeys.BASE_URL_LOCATION_PAGES,
                ApiConstants.ApiCallEpisodeKeys.BASE_URL_EPISODE_PAGES};
        for (String url : baseUrlArray) {
            networkInitialCall(url);
        }
    }

    // gets last objects from SQLite database
    private void fetchLastDbEntries() {
        appExecutors.diskIO().execute(() -> {
            lastDbCharacter = rmDatabase.getCharacterDao().showLastInCharacterList();
            lastDbLocation = rmDatabase.getLocationDao().showLastInLocationList();
            lastDbEpisode = rmDatabase.getEpisodeDao().showLastInEpisodeList();
            // get number of entries in local database
            characterEntriesDbCount = rmDatabase.getCharacterDao().getCharacterCount();
            locationEntriesDbCount = rmDatabase.getLocationDao().getLocationCount();
            episodeEntriesDbCount = rmDatabase.getEpisodeDao().getEpisodeCount();
        });
    }

    private void networkInitialCall (String url) {
        networkDataParsing.getVolleyResponse(response -> {
            try {
                JSONObject jsonObject = response.getJSONObject(ApiConstants.INFO);
                int numberOfPages = jsonObject.getInt(ApiConstants.PAGES);
                getLastEntries(numberOfPages, url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, url);
    }

    private void getLastEntries(final int numberOfPages, String url) {
        networkDataParsing.getVolleyResponse(response -> {
            //get a JSONArray from the last page and fetch the last entry object
            try {
                JSONArray jsonArray = response.getJSONArray(ApiConstants.RESULTS_ARRAY);
                int lastArrayObjectId = jsonArray.length() - 1;
                JSONObject entryObjectJson = jsonArray.getJSONObject(lastArrayObjectId);
                Object lastEntryObject = parseJsonObject(entryObjectJson, url);
                //compare entry objects from DataBase and Network, make a DB sync if necessary
                if (lastEntryObject != null) {
                    compareLastEntries(lastEntryObject, url, numberOfPages);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, url + numberOfPages);
    }

    private void compareLastEntries(Object lastEntryObject, String url, int numberOfPages) {
        if (lastEntryObject.getClass() == Character.class) {
            lastNetworkCharacter = (Character) lastEntryObject;
            if (lastNetworkCharacter.equals(lastDbCharacter)
                    && lastNetworkCharacter.getId() == characterEntriesDbCount) {
                characterTableIsUpToDate = true;
            } else {
                characterTableIsUpToDate = false;
                for (int x = 1; x < numberOfPages + 1; x++) {
                    updateDataBaseEntries(x, url);
                }
            }
        } else if (lastEntryObject.getClass() == Location.class) {
            lastNetworkLocation = (Location) lastEntryObject;
            if (lastNetworkLocation.equals(lastDbLocation)
                    && lastNetworkLocation.getId() == locationEntriesDbCount) {
                locationTableIsUpToDate = true;
            } else {
                locationTableIsUpToDate = false;
                for (int x = 1; x < numberOfPages + 1; x++) {
                    updateDataBaseEntries(x, url);
                }
            }
        } else if (lastEntryObject.getClass() == Episode.class) {
            lastNetworkEpisode = (Episode) lastEntryObject;
            if (lastNetworkEpisode.equals(lastDbEpisode)
                    && lastNetworkEpisode.getId() == episodeEntriesDbCount) {
                episodeTableIsUpToDate = true;
            } else {
                episodeTableIsUpToDate = false;
                for (int x = 1; x < numberOfPages + 1; x++) {
                    updateDataBaseEntries(x, url);
                }
            }
        }
        if (characterTableIsUpToDate && locationTableIsUpToDate && episodeTableIsUpToDate) {
            new Handler().postDelayed(() -> dbIsUpToDate.setValue(true), 1000);
            networkDataParsing.cancelVolleyRequests(url);
        }
    }

    // loops through all pages and uses data to populate Database
    private void updateDataBaseEntries(final int pageNumber, String url) {
        networkDataParsing.getVolleyResponse(response -> {
            try {
                JSONArray jsonArray = response.getJSONArray(ApiConstants.RESULTS_ARRAY);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Object newEntryObject = parseJsonObject(jsonObject, url);
                    if (newEntryObject != null) {
                        addEntryToDatabase(newEntryObject);
                    }
                }
                if (characterTableIsUpToDate && locationTableIsUpToDate && episodeTableIsUpToDate) {
                    new Handler().postDelayed(() -> dbIsUpToDate.setValue(true), 1000);
                    networkDataParsing.cancelVolleyRequests(url);
                    addJoinEntries();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, url + pageNumber);
    }

    // add a new entry to the database
    private void addEntryToDatabase (Object newEntryObject) {
        if (newEntryObject.getClass() == Character.class) {
            //adds a new Character to the database
            Character newCharacter = (Character) newEntryObject;
            if (!mCharacterList.contains(newCharacter)) {
                mCharacterList.add(newCharacter);
            }
            if (!characterTableIsUpToDate
                    && lastNetworkCharacter.getId() == mCharacterList.size()
                    && mCharacterList.contains(lastNetworkCharacter)) {
                characterTableIsUpToDate = true;
                lastDbCharacter = newCharacter;
                appExecutors.diskIO()
                        .execute(() -> rmDatabase.getCharacterDao().insertCharacterList(mCharacterList));
            }
        } else if (newEntryObject.getClass() == Location.class) {
            //adds a new Location to the database
            Location newLocation = (Location) newEntryObject;
            if (!mLocationList.contains(newLocation)) {
                mLocationList.add(newLocation);
            }
            if (!locationTableIsUpToDate
                    && lastNetworkLocation.getId() == mLocationList.size()
                    && mLocationList.contains(lastNetworkLocation)) {
                locationTableIsUpToDate = true;
                lastDbLocation = newLocation;
                appExecutors.diskIO()
                        .execute(() -> rmDatabase.getLocationDao().insertLocationList(mLocationList));
            }
        } else if (newEntryObject.getClass() == Episode.class) {
            //adds a new Episode to the database
            Episode newEpisode = (Episode) newEntryObject;
            if (!mEpisodeList.contains(newEpisode)) {
                mEpisodeList.add(newEpisode);
            }
            if (!episodeTableIsUpToDate
                    && lastNetworkEpisode.getId() == mEpisodeList.size()
                    && mEpisodeList.contains(lastNetworkEpisode)) {
                episodeTableIsUpToDate = true;
                lastDbEpisode = newEpisode;
                appExecutors.diskIO()
                        .execute(() -> rmDatabase.getEpisodeDao().insertEpisodeList(mEpisodeList));
            }
        }
    }

    private Object parseJsonObject (JSONObject entryObjectJson, String url) {
        switch (url) {
            case ApiConstants.ApiCallCharacterKeys.BASE_URL_CHARACTER_PAGES:
                Character newChar = RepoHelperUtil.parseCharacterFromJSON(entryObjectJson);
                if (MainActivity.defSystemLanguage.startsWith("ru")
                        || MainActivity.defSystemLanguage.startsWith("uk"))
                    //translate if needed
                    return UiTranslateUtils
                            .getTranslatedCharacter(mContext, Objects.requireNonNull(newChar));
                else return newChar;
            case ApiConstants.ApiCallLocationKeys.BASE_URL_LOCATION_PAGES:
                Location newLoc = RepoHelperUtil.parseLocationFromJSON(entryObjectJson);
                if (MainActivity.defSystemLanguage.startsWith("ru")
                        || MainActivity.defSystemLanguage.startsWith("uk"))
                    //translate if needed
                    return UiTranslateUtils
                            .getTranslatedLocation(mContext, Objects.requireNonNull(newLoc));
                else return newLoc;
            case ApiConstants.ApiCallEpisodeKeys.BASE_URL_EPISODE_PAGES:
                Episode newEp = RepoHelperUtil.parseEpisodeFromJSON(entryObjectJson);
                if (MainActivity.defSystemLanguage.startsWith("ru")
                        || MainActivity.defSystemLanguage.startsWith("uk"))
                    //translate if needed
                    return UiTranslateUtils
                            .getTranslatedEpisode(mContext, Objects.requireNonNull(newEp));
                else return newEp;
            default: return  null;
        }
    }

    private void addJoinEntries() {
        appExecutors.diskIO()
                .execute(() -> rmDatabase.getCharacterEpisodeJoinDao()
                        .insertCharacterEpisodeJoinList(RepoHelperUtil
                                .getCharacterEpisodeJoins(mCharacterList)));
        appExecutors.diskIO()
                .execute(() -> rmDatabase.getLocationCharacterJoinDao()
                        .insertLocationCharacterJoinList(RepoHelperUtil
                        .getLocationCharacterJoins(mLocationList)));
    }

    // calls the appropriate method based on search query and filter applied
    public LiveData<PagedList<Character>> getCharacterListFiltered(String query, int filter) {
        LiveData<PagedList<Character>> mCharacterList = new LiveData<PagedList<Character>>() {};
        String [] notDeadStatus = {mContext.getResources().getString(R.string.character_status_alive_female),
                mContext.getResources().getString(R.string.character_status_alive_male),
                mContext.getResources().getString(R.string.species_unknown)};
        if (query == null || query.isEmpty()) {
            switch (filter) {
                case 0:
                    return new LivePagedListBuilder<>(rmDatabase.getCharacterDao()
                            .getCharacterList(), 20)
                            .setFetchExecutor(appExecutors.diskIO()).build();
                case 101:
                    return new LivePagedListBuilder<>(rmDatabase.getCharacterDao()
                            .getCharacterList(notDeadStatus), 20)
                            .setFetchExecutor(appExecutors.diskIO()).build();
            }
        } else {
            switch (filter) {
                case 0:
                    return new LivePagedListBuilder<>(rmDatabase.getCharacterDao()
                            .getCharacterList("%" + query + "%"), 20)
                            .setFetchExecutor(appExecutors.diskIO()).build();
                case 101:
                    return new LivePagedListBuilder<>(rmDatabase.getCharacterDao()
                            .getCharacterList("%" + query + "%", notDeadStatus), 20)
                            .setFetchExecutor(appExecutors.diskIO()).build();
            }
        }
        return mCharacterList;
    }

    //CHARACTERS
    //gets a character by id
    public LiveData<Character> getCharacterById (int id) {
        return rmDatabase.getCharacterDao().getCharacterById(id);
    }

    //LOCATIONS
    //gets all locations
    public LiveData<PagedList<Location>> getAllLocations() {
        return new LivePagedListBuilder<>(rmDatabase.getLocationDao().showAllLocations(), 50)
                .setFetchExecutor(appExecutors.diskIO()).build();
    }

    //gets location by ID
    public @NonNull Location getLocationById (int id) {
        Location location
                = new Location(0, "unknown", "null", "null", "null");
        Future<Location> futureLocation = appExecutors.diskIO().submit(() -> {
            if (rmDatabase.getLocationDao().getLocationById(id) != null) {
                return rmDatabase.getLocationDao().getLocationById(id);
            } else { return null; }
        });
        try {
            location = futureLocation.get();
        }
        catch (ExecutionException | InterruptedException e) { e.printStackTrace();  }
        return location;
    }

    //EPISODES
    //gets all episodes
    public LiveData<PagedList<Episode>> getAllEpisodes() {
        return new LivePagedListBuilder<>(rmDatabase.getEpisodeDao().showAllEpisodes(), 20)
                .setFetchExecutor(appExecutors.diskIO()).build();
    }

    //JOIN DAOs
    //gets characters from episode
    public LiveData<List<Character>> getCharactersFromEpisode(int episodeId){
        return rmDatabase.getCharacterEpisodeJoinDao().getCharactersFromEpisode(episodeId);
    }

    //gets characters from location
    public LiveData<List<Character>> getCharactersFromLocation(int locationId) {
        return rmDatabase.getLocationCharacterJoinDao().getCharactersFromLocations(locationId);
    }

    //gets episodes from the character id
    public LiveData<List<Episode>> getEpisodesFromCharacter (int characterId) {
        return rmDatabase.getCharacterEpisodeJoinDao().getEpisodesFromCharacters(characterId);
    }

    //retrofit test
    public LiveData<Resource<CharacterModel>> databaseInit(){
        return new InitManager<CharacterModel, CharacterPageModel>() {
            @Override
            protected LiveData<ApiResponse<CharacterModel>> callLastApiModel(int lastModelId) {
                return RetrofitService
                        .getInstance()
                        .getCharacterApi()
                        .getCharacter(lastModelId);
            }

            @NonNull
            @Override
            protected LiveData<CharacterModel> getLastEntryFromDb() {
                return rmDatabase.getCharacterModelDao().showLastInCharacterList();
            }

            @NonNull
            @Override
            protected LiveData<Integer> getDbEntriesCount() {
                return rmDatabase.getCharacterModelDao().getCharacterCount();
            }

            @NonNull
            @Override
            protected LiveData<List<ApiResponse<CharacterPageModel>>> callAllPages(int pageCount) {
                Log.d(TAG, "callAllPages: ");
                List<LiveData<ApiResponse<CharacterPageModel>>> requests = new ArrayList<>();
                final ArrayList<ApiResponse<CharacterPageModel>> zippedObjects = new ArrayList<>();
                final MediatorLiveData<List<ApiResponse<CharacterPageModel>>> mediator
                        = new MediatorLiveData<>();
                for(int x = 0; x < pageCount; x++) {
                    requests.add(RetrofitService
                            .getInstance()
                            .getCharacterApi()
                            .getCharactersPage(String.valueOf(x+1)));
                }
                //zip LiveData and get a list of ApiResponse objects
                for(LiveData<ApiResponse<CharacterPageModel>> item: requests){
                    mediator.addSource(item, o -> {
                        if(!zippedObjects.contains(o)){
                            zippedObjects.add(o);
                        }
                        mediator.setValue(zippedObjects);
                    });
                }
                return mediator;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CharacterPageModel>> createCall() {
                return  RetrofitService
                        .getInstance()
                        .getCharacterApi()
                        .getCharactersPage(String.valueOf(1));
            }

            @Override
            protected void saveCallResult(List<ApiResponse.SuccessApiResponse<CharacterPageModel>> successApiResponses) {
                Log.d(TAG, "saveCallResult: CharacterPageModels count= "
                        + successApiResponses.size());
                ArrayList<CharacterModel> characterModelList = new ArrayList<>();
                for (ApiResponse.SuccessApiResponse<CharacterPageModel> characterPageModel
                        : successApiResponses) {
                    ArrayList<CharacterModel> modelList
                            = new ArrayList<>(characterPageModel.getBody().getCharacterModels());
                    for (CharacterModel characterModel : modelList) {
                        //set the timestamp to System.currentTimeMillis() in days
                        characterModel.setTimeStamp((int) (System.currentTimeMillis()/86400000));
                    }
                    characterModelList.addAll(modelList);
                }
                if (!characterModelList.isEmpty()) {
                    Log.d(TAG, "saveCallResult: first item id= "
                            + characterModelList.get(0).getId());
                    Log.d(TAG, "saveCallResult: last item id= "
                            + characterModelList.get(characterModelList.size()-1).getId());
                } else {
                    Log.d(TAG, "saveCallResult: list is empty");
                }
                appExecutors.diskIO().execute(() ->
                        rmDatabase.getCharacterModelDao().insertCharacterList(characterModelList));
            }
        }.getAsLiveData();
    }

}