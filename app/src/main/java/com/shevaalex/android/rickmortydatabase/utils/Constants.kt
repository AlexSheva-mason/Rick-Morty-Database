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
        const val DB_REFETCH_PERIOD = 45

        //sets the db sync status check period (hours)
        const val DB_CHECK_PERIOD = 24

        //Share type image
        const val SHARE_TYPE = "image/*"

        //SavedInstanceState keys
        const val KEY_ACTIVITY_MAIN_DB_SYNC_BOOL = "main_activity_dbsync"
        const val KEY_FRAGMENT_CHAR_LIST_QUERY = "fragment_character_list_query"
        const val KEY_FRAGMENT_CHAR_LIST_FILTER_MAP = "fragment_character_list_filter_map"
        const val KEY_FRAGMENT_CHAR_LIST_LIST_POSITION = "fragment_character_list_list_position"
        const val KEY_FRAGMENT_LOCATION_LIST_QUERY = "fragment_location_list_query"
        const val KEY_FRAGMENT_LOCATION_LIST_FILTER_MAP = "fragment_location_list_filter_map"
        const val KEY_FRAGMENT_LOCATION_LIST_LIST_POSITION = "fragment_location_list_list_position"
        const val KEY_FRAGMENT_EPISODE_LIST_QUERY = "fragment_episode_list_query"
        const val KEY_FRAGMENT_EPISODE_LIST_FILTER_MAP = "fragment_episode_list_filter_map"
        const val KEY_FRAGMENT_EPISODE_LIST_LIST_POSITION = "fragment_episode_list_list_position"
        const val KEY_FRAGMENT_CHAR_DETAIL_OBJECT = "fragment_character_detail_object"

        //Shared Prefs keys
        const val KEY_ACTIVITY_MAIN_DB_SYNCED_TIMESTAMP = "main_activity_bool_true_timestamp"

        //SearchView recent queries list size
        const val SV_RECENT_COUNT = 10

        //Character List Filter map keys
        const val KEY_MAP_FILTER_STATUS_ALIVE_F = "status_alive_female"
        const val KEY_MAP_FILTER_STATUS_ALIVE_M = "status_alive_male"
        const val KEY_MAP_FILTER_STATUS_DEAD_F = "status_dead_female"
        const val KEY_MAP_FILTER_STATUS_DEAD_M = "status_dead_male"
        const val KEY_MAP_FILTER_STATUS_UNKNOWN = "status_unknown"

        const val KEY_MAP_FILTER_GENDER_FEMALE = "gender_female"
        const val KEY_MAP_FILTER_GENDER_MALE = "gender_male"
        const val KEY_MAP_FILTER_GENDER_GENDERLESS = "gender_genderless"
        const val KEY_MAP_FILTER_GENDER_UNKNOWN = "gender_unknown"

        const val KEY_MAP_FILTER_SPECIES_ALL = "species_all"
        const val KEY_MAP_FILTER_SPECIES_HUMAN = "species_human"
        const val KEY_MAP_FILTER_SPECIES_HUMANOID = "species_humanoid"
        const val KEY_MAP_FILTER_SPECIES_ALIEN = "species_alien"
        const val KEY_MAP_FILTER_SPECIES_ANIMAL = "species_animal"
        const val KEY_MAP_FILTER_SPECIES_ROBOT = "species_robot"
        const val KEY_MAP_FILTER_SPECIES_POOPY = "species_poopy"
        const val KEY_MAP_FILTER_SPECIES_CRONENBERG = "species_cronenberg"
        const val KEY_MAP_FILTER_SPECIES_MYTH = "species_myth"

        //Location List Filter map keys
        const val KEY_MAP_FILTER_LOC_TYPE_ALL = "loc_type_all"
        const val KEY_MAP_FILTER_LOC_TYPE_PLANET = "loc_type_planet"
        const val KEY_MAP_FILTER_LOC_TYPE_SPACE_ST = "loc_type_space_station"
        const val KEY_MAP_FILTER_LOC_TYPE_MICRO = "loc_type_microverse"

        const val KEY_MAP_FILTER_LOC_DIMENS_ALL = "loc_dimens_all"
        const val KEY_MAP_FILTER_LOC_DIMENS_REPLACE = "loc_dimens_replacement"
        const val KEY_MAP_FILTER_LOC_DIMENS_C_137 = "loc_dimens_c-137"
        const val KEY_MAP_FILTER_LOC_DIMENS_CRONENBERG = "loc_dimens_cronenberg"
        const val KEY_MAP_FILTER_LOC_DIMENS_UNKNOWN = "loc_dimens_unknown"

        //Episode List Filter map keys
        const val KEY_MAP_FILTER_EPISODE_S_ALL = "episode_season_all"
        const val KEY_MAP_FILTER_EPISODE_S_01 = "episode_season_s01"
        const val KEY_MAP_FILTER_EPISODE_S_02 = "episode_season_s02"
        const val KEY_MAP_FILTER_EPISODE_S_03 = "episode_season_s03"
        const val KEY_MAP_FILTER_EPISODE_S_04 = "episode_season_s04"

    }
}