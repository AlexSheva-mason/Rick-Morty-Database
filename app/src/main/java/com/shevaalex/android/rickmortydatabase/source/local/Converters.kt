package com.shevaalex.android.rickmortydatabase.source.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocation

object Converters {
    @JvmStatic
    @TypeConverter
    fun listToString(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @JvmStatic
    @TypeConverter
    fun stringToList(json: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @JvmStatic
    @TypeConverter
    fun linkedLocationModelToString(linkedLocation: LinkedLocation): String {
        return Gson().toJson(linkedLocation)
    }

    @JvmStatic
    @TypeConverter
    fun stringToLinkedLocationModel(string: String): LinkedLocation {
        return Gson().fromJson(string, LinkedLocation::class.java)
    }
}