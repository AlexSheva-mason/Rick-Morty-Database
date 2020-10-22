package com.shevaalex.android.rickmortydatabase.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel;
import com.shevaalex.android.rickmortydatabase.models.location.LocationModel;
import com.shevaalex.android.rickmortydatabase.source.database.Character;
import com.shevaalex.android.rickmortydatabase.source.database.Episode;
import com.shevaalex.android.rickmortydatabase.source.database.Location;

//translates the data retrieved by the Room database and returns it to the recyclerview adapter
public class UiTranslateUtils {

    public static ApiObjectModel getTranslatedObject (Context context, @NonNull ApiObjectModel model) {
        if (model instanceof CharacterModel) return getTranslatedCharacter(context, (CharacterModel) model);
        if (model instanceof LocationModel) return getTranslatedLocation(context, (LocationModel) model);
        if (model instanceof EpisodeModel) return getTranslatedEpisode(context, (EpisodeModel) model);
        return model;
    }

    //CHARACTER model data
    public static CharacterModel getTranslatedCharacter (Context context, @NonNull CharacterModel character) {
        character.setName(getCharacterNameLocalized(context, character));
        character.setSpecies(getCharacterSpeciesLocalized(context, character));
        character.setStatus(getCharacterStatusLocalized(context, character));
        character.setGender(getCharacterGenderLocalized(context, character));
        return character;
    }

    private static String getCharacterNameLocalized(Context context, @NonNull CharacterModel character) {
        String translatedName = StringParsing.returnCharacterNameLocale(context, character.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return character.getName();
    }

    private static String getCharacterGenderLocalized (Context context, @NonNull CharacterModel character) {
        String translatedGender = StringParsing.returnCharacterGenderLocale(context, character.getGender());
        if (!translatedGender.equals(StringParsing.KEY_NULL)) {
            return translatedGender;
        }
        return character.getGender();
    }

    private static String getCharacterSpeciesLocalized (Context context, @NonNull CharacterModel character) {
        String translatedSpecies = StringParsing.returnCharacterSpeciesLocale(context, character.getSpecies());
        if (!translatedSpecies.equals(StringParsing.KEY_NULL)) {
            return translatedSpecies;
        }
        return character.getSpecies();
    }

    private static String getCharacterStatusLocalized (Context context, @NonNull CharacterModel character) {
        String translatedStatus = StringParsing.returnCharacterStatusLocale(context, character.getGender(), character.getStatus());
        if (!translatedStatus.equals(StringParsing.KEY_NULL)) {
            return translatedStatus;
        }
        return character.getStatus();
    }

    //LOCATION model data
    public static LocationModel getTranslatedLocation (Context context, @NonNull LocationModel location) {
        location.setName(getLocationNameLocalized(context, location));
        location.setDimension(getLocationDimensionLocalized(context, location));
        location.setType(getLocationTypeLocalized(context, location));
        return location;
    }

    private static String getLocationNameLocalized (Context context, @NonNull LocationModel location) {
        String translatedName = StringParsing.returnLocationNameLocale(context, location.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return location.getName();
    }

    private static String getLocationDimensionLocalized (Context context, @NonNull LocationModel location) {
        String translatedDimension = StringParsing.returnLocationDimensionLocale(context, location.getDimension());
        if (!translatedDimension.equals(StringParsing.KEY_NULL)) {
            return translatedDimension;
        }
        return location.getDimension();
    }

    private static String getLocationTypeLocalized (Context context, @NonNull LocationModel location) {
        String translatedType = StringParsing.returnLocationTypeLocale(context, location.getType());
        if (!translatedType.equals(StringParsing.KEY_NULL)) {
            return translatedType;
        }
        return location.getType();
    }

    //EPISODE model data
    public static EpisodeModel getTranslatedEpisode (Context context, @NonNull EpisodeModel episode) {
        episode.setName(getEpisodeNameLocalized(context, episode));
        episode.setAirDate(getEpisodeAirDateLocalized(context, episode));
        return episode;
    }

    private static String getEpisodeNameLocalized (Context context, @NonNull EpisodeModel episode) {
        String translatedName = StringParsing.returnEpisodeNameLocale(context, episode.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return episode.getName();
    }

    private static String getEpisodeAirDateLocalized (Context context, @NonNull EpisodeModel episode) {
        String translatedAirDate = StringParsing.returnEpisodeAirDate(context, episode.getAirDate());
        if (!translatedAirDate.equals(StringParsing.KEY_NULL)) {
            return translatedAirDate;
        }
        return episode.getAirDate();
    }

    //CHARACTER data
    public static Character getTranslatedCharacter (Context context, @NonNull Character character) {
        character.setName(getCharacterNameLocalized(context, character));
        character.setSpecies(getCharacterSpeciesLocalized(context, character));
        character.setStatus(getCharacterStatusLocalized(context, character));
        character.setGender(getCharacterGenderLocalized(context, character));
        return character;
    }

    private static String getCharacterNameLocalized(Context context, @NonNull Character character) {
        String translatedName = StringParsing.returnCharacterNameLocale(context, character.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return character.getName();
    }

    private static String getCharacterGenderLocalized (Context context, @NonNull Character character) {
        String translatedGender = StringParsing.returnCharacterGenderLocale(context, character.getGender());
        if (!translatedGender.equals(StringParsing.KEY_NULL)) {
            return translatedGender;
        }
        return character.getGender();
    }

    private static String getCharacterSpeciesLocalized (Context context, @NonNull Character character) {
        String translatedSpecies = StringParsing.returnCharacterSpeciesLocale(context, character.getSpecies());
        if (!translatedSpecies.equals(StringParsing.KEY_NULL)) {
            return translatedSpecies;
        }
        return character.getSpecies();
    }

    private static String getCharacterStatusLocalized (Context context, @NonNull Character character) {
        String translatedStatus = StringParsing.returnCharacterStatusLocale(context, character.getGender(), character.getStatus());
        if (!translatedStatus.equals(StringParsing.KEY_NULL)) {
            return translatedStatus;
        }
        return character.getStatus();
    }

    // LOCATION data
    public static Location getTranslatedLocation (Context context, @NonNull Location location) {
        location.setName(getLocationNameLocalized(context, location));
        location.setDimension(getLocationDimensionLocalized(context, location));
        location.setType(getLocationTypeLocalized(context, location));
        return location;
    }

    private static String getLocationNameLocalized (Context context, @NonNull Location location) {
        String translatedName = StringParsing.returnLocationNameLocale(context, location.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return location.getName();
    }

    private static String getLocationDimensionLocalized (Context context, @NonNull Location location) {
        String translatedDimension = StringParsing.returnLocationDimensionLocale(context, location.getDimension());
        if (!translatedDimension.equals(StringParsing.KEY_NULL)) {
            return translatedDimension;
        }
        return location.getDimension();
    }

    private static String getLocationTypeLocalized (Context context, @NonNull Location location) {
        String translatedType = StringParsing.returnLocationTypeLocale(context, location.getType());
        if (!translatedType.equals(StringParsing.KEY_NULL)) {
            return translatedType;
        }
        return location.getType();
    }

    // EPISODE data
    public static Episode getTranslatedEpisode (Context context, @NonNull Episode episode) {
        episode.setName(getEpisodeNameLocalized(context, episode));
        episode.setAirDate(getEpisodeAirDateLocalized(context, episode));
        return episode;
    }

    private static String getEpisodeNameLocalized (Context context, @NonNull Episode episode) {
        String translatedName = StringParsing.returnEpisodeNameLocale(context, episode.getId());
        if (!translatedName.equals(StringParsing.KEY_NULL)) {
            return translatedName;
        }
        return episode.getName();
    }

    private static String getEpisodeAirDateLocalized (Context context, @NonNull Episode episode) {
        String translatedAirDate = StringParsing.returnEpisodeAirDate(context, episode.getAirDate());
        if (!translatedAirDate.equals(StringParsing.KEY_NULL)) {
            return translatedAirDate;
        }
        return episode.getAirDate();
    }

}