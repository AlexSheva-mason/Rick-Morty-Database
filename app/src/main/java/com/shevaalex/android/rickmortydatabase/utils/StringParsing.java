package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;

import com.shevaalex.android.rickmortydatabase.source.network.ApiCall;

import java.util.ArrayList;

public abstract class StringParsing {
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

    public static String returnNameLocale (Context context, int id) {
        String characterIdToMatch = "id_" + id;
        try {
            int resId = context.getResources().getIdentifier(characterIdToMatch, "string" , context.getPackageName());
            return context.getResources().getString(resId);
        } catch(Exception e){
            return null;
        }
    }
}

