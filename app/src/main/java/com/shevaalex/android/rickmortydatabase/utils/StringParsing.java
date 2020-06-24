package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;

import com.shevaalex.android.rickmortydatabase.R;
import com.shevaalex.android.rickmortydatabase.source.network.ApiCall;

import java.util.ArrayList;

public abstract class StringParsing {
    private static final String CHAR_GENDER_FEMALE = "female";
    private static final String CHAR_GENDER_MALE = "male";
    private static final String CHAR_GENDER_GENDERLESS = "genderless";
    private static final String CHAR_GENDER_UNKNOWN = "unknown";
    private static final String CHAR_STATUS_ALIVE = "alive";
    private static final String CHAR_STATUS_DEAD = "dead";
    private static final String CHARACTER_RES_KEY = "character_";
    private static final String SPECIES_RES_KEY = "species_";
    private static final String LOCATION_RES_KEY = "location_";
    private static final String EPISODE_RES_KEY = "episode_";


    public static ArrayList<Integer> parseIdsFromString(String stringWirhUrls) {
        ArrayList<Integer> parsedIdList = new ArrayList<>();
        if (stringWirhUrls.contains(",")) {
            String[] splitArray = stringWirhUrls.split(",");
            for (String arrayElement : splitArray) {
                int slashIndex = arrayElement.lastIndexOf("/");
                int quoteIndex = arrayElement.lastIndexOf("\"");
                int parsedId = Integer.parseInt(arrayElement.substring(slashIndex+1, quoteIndex));
                parsedIdList.add(parsedId);
            }
        } else if (stringWirhUrls.contains("/")){
            int slashIndex = stringWirhUrls.lastIndexOf("/");
            int quoteIndex = stringWirhUrls.lastIndexOf("\"");
            int parsedId = Integer.parseInt(stringWirhUrls.substring(slashIndex+1, quoteIndex));
            parsedIdList.add(parsedId);
        }
        return parsedIdList;
    }

    public static int parseLocationId (String locationUrl) {
        int lastKnownLocId = 0;
        if (locationUrl.contains(ApiCall.ApiCallCharacterKeys.CHARACTER_LOCATIONS_SUBSTRING)) {
            int slashId = locationUrl.lastIndexOf("/");
            lastKnownLocId = Integer.parseInt(locationUrl.substring(slashId+1));
        }
        return lastKnownLocId;
    }

    public static String parseCharacterName (String characterName) {
        return (characterName.trim().replaceAll("\\s", "_") + "_");
    }

    public static String returnCharacterNameLocale(Context context, int id) {
        String characterIdToMatch = CHARACTER_RES_KEY + id;
        try {
            int resId = context.getResources().getIdentifier(characterIdToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnGenderLocale (Context context, String gender) {
        gender = gender.toLowerCase().trim();
        if (gender.contains(CHAR_GENDER_FEMALE)) {
            return context.getResources().getString(R.string.character_gender_female);
        } else if (gender.contains(CHAR_GENDER_MALE)) {
            return context.getResources().getString(R.string.character_gender_male);
        } else if (gender.contains(CHAR_GENDER_GENDERLESS)) {
            return context.getResources().getString(R.string.character_gender_genderless);
        } else {
            return context.getResources().getString(R.string.character_gender_unknown);
        }
    }

    public static String returnStatusLocale (Context context, String gender, String status) {
        status = status.toLowerCase().trim();
        gender = gender.toLowerCase().trim();
        String statusTranslated = context.getResources().getString(R.string.character_gender_unknown);
        final boolean b = gender.contains(CHAR_GENDER_MALE) || gender.contains(CHAR_GENDER_GENDERLESS) || gender.contains(CHAR_GENDER_UNKNOWN);
        if (status.contains(CHAR_STATUS_ALIVE)) {
            if (gender.contains(CHAR_GENDER_FEMALE)) {
                statusTranslated = context.getResources().getString(R.string.character_status_alive_female);
            } else if (b) {
                statusTranslated = context.getResources().getString(R.string.character_status_alive_male);
            }
        } else if (status.contains(CHAR_STATUS_DEAD)) {
            if (gender.contains(CHAR_GENDER_FEMALE)) {
                statusTranslated = context.getResources().getString(R.string.character_status_dead_female);
            } else if (b) {
                statusTranslated = context.getResources().getString(R.string.character_status_dead_male);
            }
        }
        return statusTranslated;
    }

    public static String returnSpeciesLocale (Context context, String speciesEn) {
        String speciesEnToMatch = SPECIES_RES_KEY + speciesEn;
        try {
            int resId = context.getResources().getIdentifier(speciesEnToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnLocationNameLocale(Context context, int id) {
        String locationIdToMatch = LOCATION_RES_KEY + id;
        try {
            int resId = context.getResources().getIdentifier(locationIdToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnTypeLocale (Context context, String type) {
        String typeToMatch = LOCATION_RES_KEY + type.replaceAll("\\s", "_").replaceAll("\\(", "_")
                .replaceAll("\\)", "_");
        try {
            int resId = context.getResources().getIdentifier(typeToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnDimensionLocale (Context context, String dimension) {
        if (dimension.contains("J19ζ7")) {
            return "J19ζ7";
        }
        String dimensionToMatch = LOCATION_RES_KEY + dimension.replaceAll("\\s", "_").replaceAll("\\(", "_")
                .replaceAll("\\)", "_").replaceAll("'", "_").replaceAll("-", "_");
        try {
            int resId = context.getResources().getIdentifier(dimensionToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnEpisodeNameLocale (Context context, int id) {
        String episodeIdToMatch = EPISODE_RES_KEY + id;
        try {
            int resId = context.getResources().getIdentifier(episodeIdToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return "null";
        }
    }

    public static String returnEpisodeAirDate (Context context, String airDate) {
        String monthToMatch = EPISODE_RES_KEY + airDate.replaceAll("[0-9]", "")
                .replaceAll("\\s", "").replaceAll(",", "").toLowerCase();
        try {
            int resId = context.getResources().getIdentifier(monthToMatch, "string" , context.getPackageName());
            String monthTranslated = context.getResources().getString(resId);
            String[] splitDate = airDate.replaceAll("[a-zA-Z]", "").split(",");
            return splitDate[0] + " " + monthTranslated + splitDate[1];
        } catch(Exception e){
            return "null";
        }

    }
}

