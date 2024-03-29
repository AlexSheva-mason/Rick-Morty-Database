package com.shevaalex.android.rickmortydatabase.utils


class Constants {
    companion object{
        const val DATABASE_NAME = "rmdatabase"

        //Settings shared preferences keys
        const val LIST_THEME_PREFERENCE_KEY = "theme_list"
        const val SWITCH_THEME_PREFERENCE_KEY = "theme_switch"
        const val KEY_VERSION = "version"
        const val KEY_REVIEW = "review"

        //Firebase events and keys
        const val SETTINGS_EVENT_THEME_SELECT = "settings_theme_selected"
        const val SETTINGS_KEY_THEME_MODE = "theme_mode"
        const val SETTINGS_EVENT_PREFERENCE_CLICK = "settings_preference_clicked"
        const val SETTINGS_KEY_PREFERENCE_NAME = "preference_name"
        const val INIT_INSTALLER_NAME = "installer_name"
        const val INIT_INSTALLER_NAME_PARAM_KEY = "installer_package_name"

        //Settings data
        const val DATA_REVIEW_LINK = "http://play.google.com/store/apps/details?id=com.shevaalex.android.rickmortydatabase"

        //Retrofit options
        const val CONNECTION_TIMEOUT = 10L
        const val READ_TIMEOUT = 10L
        const val WRITE_TIMEOUT = 10L

        //Room pagination page size
        const val ROOM_PAGE_SIZE = 75

        //object refetch period (hours)
        const val OBJECT_REFETCH_PERIOD = 48

        //firebase auth token refresh time (mins)
        const val AUTH_TOKEN_REFRESH_TIME = 50

        //sets the required number of app launched events between google review dialogs
        const val REVIEW_REQUIRED_NUMBER_APP_LAUNCHED = 6

        //sets the amount of time to be elapsed between google review dialog can be shown again (days)
        const val REVIEW_REQ_SHOW_PERIOD = 60

        //Share type image
        const val SHARE_TYPE = "image/*"

        //SavedInstanceState keys
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
        const val KEY_FRAGMENT_LOCATION_DETAIL_OBJECT = "fragment_location_detail_object"
        const val KEY_FRAGMENT_EPISODE_DETAIL_OBJECT = "fragment_episode_detail_object"

        //Shared Prefs keys
        const val SHARED_PREFS_FILE_NAME = "com.shevaalex.android.rickmortydatabase.shared.prefs"
        const val KEY_APP_LAUNCH_NUMBER = "app_launched_number"
        const val KEY_REVIEW_ASKED_FOR_REVIEW_TIMESTAMP = "review_asked_for_review_timestamp"
        const val KEY_AUTH_TOKEN = "firebase_auth_token"
        const val KEY_INIT_VM_CHARACTERS_FETCHED_TIMESTAMP = "init_viewmodel_characters_refetched_timestamp"
        const val KEY_INIT_VM_LOCATIONS_FETCHED_TIMESTAMP = "init_viewmodel_locations_refetched_timestamp"
        const val KEY_INIT_VM_EPISODES_FETCHED_TIMESTAMP = "init_viewmodel_episodes_refetched_timestamp"
        const val KEY_APP_FIRST_LAUCH = "com.shevaalex.android.rickmortydatabase_app_first_launch"

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

        //Episode List Filter map keys + values
        const val KEY_MAP_FILTER_EPISODE_S_ALL = "episode_season_all"
        const val KEY_MAP_FILTER_EPISODE_S_01 = "episode_season_s01"
        const val KEY_MAP_FILTER_EPISODE_S_02 = "episode_season_s02"
        const val KEY_MAP_FILTER_EPISODE_S_03 = "episode_season_s03"
        const val KEY_MAP_FILTER_EPISODE_S_04 = "episode_season_s04"
        const val VALUE_MAP_FILTER_EPISODE_S_01 = "S01"
        const val VALUE_MAP_FILTER_EPISODE_S_02 = "S02"
        const val VALUE_MAP_FILTER_EPISODE_S_03 = "S03"
        const val VALUE_MAP_FILTER_EPISODE_S_04 = "S04"

        //Transition names
        const val TRANSITION_CHARACTER = "character_transition_"
        const val TRANSITION_LOCATION = "location_transition_"
        const val TRANSITION_EPISODE = "episode_transition_"

    }
}