package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;

import com.shevaalex.android.rickmortydatabase.source.database.CharacterSmall;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

public class UiTranslateUtils {

    //CHARACTER data
    public static String getCharacterNameLocalized(Context context, CharacterSmall character) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedName = StringParsing.returnCharacterNameLocale(context, character.getId());
            if (!translatedName.equals(StringParsing.KEY_NULL)) {
                return translatedName;
            }
        }
        return character.getName();
    }

    public static String getCharacterGenderLocalized (Context context, CharacterSmall character) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedGender = StringParsing.returnCharacterGenderLocale(context, character.getGender());
            if (!translatedGender.equals(StringParsing.KEY_NULL)) {
                return translatedGender;
            }
        }
        return character.getGender();
    }

    public static String getCharacterSpeciesLocalized (Context context, CharacterSmall character) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedSpecies = StringParsing.returnCharacterSpeciesLocale(context, character.getSpecies());
            if (!translatedSpecies.equals(StringParsing.KEY_NULL)) {
                return translatedSpecies;
            }
        }
        return character.getSpecies();
    }

    public static String getCharacterStatusLocalized (Context context, CharacterSmall character) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedStatus = StringParsing.returnCharacterStatusLocale(context, character.getGender(), character.getStatus());
            if (!translatedStatus.equals(StringParsing.KEY_NULL)) {
                return translatedStatus;
            }
        }
        return character.getStatus();
    }

    // LOCATION data
    public static String getLocationNameLocalized (Context context, Location location) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedName = StringParsing.returnLocationNameLocale(context, location.getId());
            if (!translatedName.equals(StringParsing.KEY_NULL)) {
                return translatedName;
            }
        }
        return location.getName();
    }

    public static String getLocationDimensionLocalized (Context context, Location location) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedDimension = StringParsing.returnLocationDimensionLocale(context, location.getDimension());
            if (!translatedDimension.equals(StringParsing.KEY_NULL)) {
                return translatedDimension;
            }
        }
        return location.getDimension();
    }

    public static String getLocationTypeLocalized (Context context, Location location) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedType = StringParsing.returnLocationTypeLocale(context, location.getType());
            if (!translatedType.equals(StringParsing.KEY_NULL)) {
                return translatedType;
            }
        }
        return location.getType();
    }

    // EPISODE data
    public static String getEpisodeNameLocalized (Context context, Episode episode) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
           String translatedName = StringParsing.returnEpisodeNameLocale(context, episode.getId());
           if (!translatedName.equals(StringParsing.KEY_NULL)) {
               return translatedName;
           }
        }
        return episode.getName();
    }

    public static String getEpisodeAirDateLocalized (Context context, Episode episode) {
        String locale = LocaleUtils.getAppLocale(context);
        if (locale.startsWith("ru") || locale.startsWith("uk")) {
            String translatedAirDate = StringParsing.returnEpisodeAirDate(context, episode.getAirDate());
            if (!translatedAirDate.equals(StringParsing.KEY_NULL)) {
                return translatedAirDate;
            }
        }
        return episode.getAirDate();
    }

}