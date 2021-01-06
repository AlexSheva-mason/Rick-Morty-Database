package com.shevaalex.android.rickmortydatabase.utils.networking;


public abstract class ApiConstants {
    public static final String INFO = "info";
    public static final String RESULTS_ARRAY = "results";
    public static final String BASE_URL = "https://rickandmortyapi.com/api/";
    public static final String KEY_QUERY_PAGE = "page";

    public static class ApiCallCharacterKeys {
        public static final String SUB_URL_CHARACTER = "character/";

        public static final String CHARACTER_ORIGIN_LOCATION = "origin";
        public static final String CHARACTER_LAST_LOCATION = "location";
        public static final String CHARACTER_IMAGE_URL = "image";
        public static final String CHARACTER_EPISODE_LIST = "episode";
    }

    public static class ApiCallLocationKeys {
        public static final String SUB_URL_LOCATION = "location/";
        public static final String LOCATION_RESIDENTS = "residents";
    }

    public static class ApiCallEpisodeKeys {
        public static final String SUB_URL_EPISODE = "episode/";
        public static final String EPISODE_AIR_DATE = "air_date";
        public static final String EPISODE_CODE = "episode";
        public static final String EPISODE_CHARACTERS = "characters";
    }
}
