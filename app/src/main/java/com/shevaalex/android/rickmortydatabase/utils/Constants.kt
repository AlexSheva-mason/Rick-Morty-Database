package com.shevaalex.android.rickmortydatabase.utils

class Constants {
    companion object{
        const val DATABASE_NAME = "rmdatabase"

        //Settings shared preferences keys
        const val LIST_THEME_PREFERENCE_KEY = "theme_list"
        const val SWITCH_THEME_PREFERENCE_KEY = "theme_switch"
        const val KEY_VERSION = "version"

        //Retrofit options
        const val CONNECTION_TIMEOUT = 10L
        const val READ_TIMEOUT = 10L
        const val WRITE_TIMEOUT = 10L

        //sets the db entry update period (days)
        const val REFRESH_CONSTANT = 45

        //SavedInstanceState keys
        const val KEY_ACTIVITY_MAIN_DB_SYNC_BOOL = "main_activity_dbsync"

        //SearchView recent queries list size
        const val SV_RECENT_COUNT = 10
    }
}