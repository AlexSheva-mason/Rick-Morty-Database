package com.shevaalex.android.rickmortydatabase.networking;

public class ApiCall {
    public static final String BASE_URL = "https://rickandmortyapi.com/api/";
    public static final String BASE_URL_EPISODE = "https://rickandmortyapi.com/api/episode/";
    public static final String BASE_URL_LOCATION = "https://rickandmortyapi.com/api/location/";

    //TODO add subclasses for Locations, Episodes and separate api urls and keys for calls
    public static class ApiCallCharacterKeys {
        public static final String BASE_URL_CHARACTER_PAGES = "https://rickandmortyapi.com/api/character/?page=";
        public static final String CHARACTER_INFO = "info";
        public static final String CHARACTER_ARRAY = "results";
        public static final String CHARACTER_PAGES = "pages";

        public static final String CHARACTER_ID = "id";
        public static final String CHARACTER_NAME = "name";
        public static final String CHARACTER_STATUS = "status";
        public static final String CHARACTER_SPECIES = "species";
        public static final String CHARACTER_TYPE = "type";
        public static final String CHARACTER_GENDER = "gender";
        public static final String CHARACTER_ORIGIN_LOCATION = "origin";
        public static final String CHARACTER_LAST_LOCATION = "location";
        public static final String CHARACTER_IMAGE_URL = "image";
        public static final String CHARACTER_EPISODE_LIST = "episode";
        public static final String CHARACTER_URL = "url";
        public static final String CHARACTER_TIME_CREATED = "created";
    }
}
