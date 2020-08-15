package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel;

import java.lang.reflect.Type;

public class Converters {
    @TypeConverter
    public static String stringArrayToString(String[] array) {
        return new Gson().toJson(array);
    }

    @TypeConverter
    public static String[] stringToArray(String json) {
        Type arrayType = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(json, arrayType);
    }

    @TypeConverter
    public static String linkedLocationModelToString(LinkedLocationModel locationModel) {
        return new Gson().toJson(locationModel);
    }

    @TypeConverter
    public static LinkedLocationModel stringToLinkedLocationModel(String string) {
        return new Gson().fromJson(string, LinkedLocationModel.class);
    }
}
