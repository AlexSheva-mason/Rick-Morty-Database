package com.shevaalex.android.rickmortydatabase.source.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shevaalex.android.rickmortydatabase.models.character.LinkedLocationModel

object Converters {
    @JvmStatic
    @TypeConverter
    fun listToString(list: List<String>): String {
        return Gson().toJson(list)
    }

    @JvmStatic
    @TypeConverter
    fun stringToList(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @JvmStatic
    @TypeConverter
    fun linkedLocationModelToString(locationModel: LinkedLocationModel): String {
        return Gson().toJson(locationModel)
    }

    @JvmStatic
    @TypeConverter
    fun stringToLinkedLocationModel(string: String): LinkedLocationModel {
        return Gson().fromJson(string, LinkedLocationModel::class.java)
    }
}