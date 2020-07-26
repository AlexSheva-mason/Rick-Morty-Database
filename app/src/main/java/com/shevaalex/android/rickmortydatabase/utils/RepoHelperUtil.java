package com.shevaalex.android.rickmortydatabase.utils;

import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.CharacterEpisodeJoin;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;
import com.shevaalex.android.rickmortydatabase.source.database.LocationCharacterJoin;
import com.shevaalex.android.rickmortydatabase.source.network.ApiCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/*
Helper class for parsing Json objects, join entities etc for Repository class
 */
public abstract class RepoHelperUtil {

    //returns a Character object from JSONObject
    public static Character parseCharacterFromJSON(JSONObject entryObjectJson) {
        try {
            int id = entryObjectJson.
                    getInt(ApiCall.ApiCallCharacterKeys.CHARACTER_ID);
            String name = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_NAME);
            String status = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_STATUS);
            String species = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_SPECIES);
            String type = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_TYPE);
            String gender = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_GENDER);
            String imgUrl = entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_IMAGE_URL);
            String episodeList = StringParsing.returnStringOfIds(entryObjectJson.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_EPISODE_LIST));
            // Parse last known and origin locations strings to retreive IDs
            JSONObject originLocation = entryObjectJson.
                    getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_ORIGIN_LOCATION);
            String originLocString = originLocation.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_URL);
            int originLocId = StringParsing.parseLocationId(originLocString);
            JSONObject lastKnownLoc = entryObjectJson.
                    getJSONObject(ApiCall.ApiCallCharacterKeys.CHARACTER_LAST_LOCATION);
            String lastKnownLocString = lastKnownLoc.
                    getString(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_URL);
            int lastKnownLocId = StringParsing.parseLocationId(lastKnownLocString);
            // Return a Character object
            return new Character(id, name, status, species, type, gender, originLocId,
                    lastKnownLocId, imgUrl, episodeList);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns a Location object from JSONObject
    public static Location parseLocationFromJSON(JSONObject entryObjectJson) {
        try {
            int id = entryObjectJson.
                    getInt(ApiCall.ApiCallLocationKeys.LOCATION_ID);
            String name = entryObjectJson.
                    getString(ApiCall.ApiCallLocationKeys.LOCATION_NAME);
            String type = entryObjectJson.
                    getString(ApiCall.ApiCallLocationKeys.LOCATION_TYPE);
            String dimension = entryObjectJson.
                    getString(ApiCall.ApiCallLocationKeys.LOCATION_DIMENSION);
            String residents = StringParsing.returnStringOfIds(entryObjectJson
                    .getString(ApiCall.ApiCallLocationKeys.LOCATION_RESIDENTS));
            return new Location(id, name, type, dimension, residents);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns an Episode object from JSONObject
    public static Episode parseEpisodeFromJSON(JSONObject entryObjectJson) {
        try {
            int id = entryObjectJson.
                    getInt(ApiCall.ApiCallEpisodeKeys.EPISODE_ID);
            String name = entryObjectJson.
                    getString(ApiCall.ApiCallEpisodeKeys.EPISODE_NAME);
            String airDate = entryObjectJson.
                    getString(ApiCall.ApiCallEpisodeKeys.EPISODE_AIR_DATE);
            String code = entryObjectJson.
                    getString(ApiCall.ApiCallEpisodeKeys.EPISODE_CODE);
            String characters = StringParsing.
                    returnStringOfIds(entryObjectJson
                            .getString(ApiCall.ApiCallEpisodeKeys.EPISODE_CHARACTERS));
            return new Episode(id, name, airDate, code, characters);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //receives a list of Characters, parses and returns a list of CharacterEpisodeJoins
    public static ArrayList<CharacterEpisodeJoin>
    getCharacterEpisodeJoins(ArrayList<Character> characterList) {
        ArrayList<CharacterEpisodeJoin> characterEpisodeJoins = new ArrayList<>();
        for (Character character : characterList) {
            ArrayList<Integer> episodeIds
                    = StringParsing.parseIdsFromString(character.getEpisodeList());
            if (episodeIds.size() > 0) {
                for (int episodeID : episodeIds) {
                    characterEpisodeJoins
                            .add(new CharacterEpisodeJoin(character.getId(), episodeID));
                }
            }
        }
        return characterEpisodeJoins;
    }

    //receives a list of Locations, parses and returns a list of LocationCharacterJoins
    public static ArrayList<LocationCharacterJoin>
    getLocationCharacterJoins(ArrayList<Location> locationList) {
        ArrayList<LocationCharacterJoin> locationCharacterJoins = new ArrayList<>();
        for (Location location : locationList) {
            ArrayList<Integer> residentsIds = StringParsing.parseIdsFromString(location.getResidentsList());
            if (residentsIds.size() > 0) {
                for (int residentId : residentsIds) {
                    locationCharacterJoins.add(new LocationCharacterJoin(residentId, location.getId()));
                }
            }
        }
        return locationCharacterJoins;
    }

}