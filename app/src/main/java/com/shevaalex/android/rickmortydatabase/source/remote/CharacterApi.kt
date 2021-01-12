package com.shevaalex.android.rickmortydatabase.source.remote

import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.utils.networking.*
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApi {

    /**
     * gets a list of all children from the character node
     * @param idToken token to be used in Query ?auth=<idToken>
     * @param isShallow boolean is used in Query &shallow=isShallow. Pass true to get shallow result
     */
    @GET(SUB_URL_CHARACTER + FIREBASE_JSON_SUFFIX)
    suspend fun getCharacterList(
            @Query(FIREBASE_AUTH_QUERY) idToken: String,
            @Query(FIREBASE_SHALLOW_QUERY) isShallow: Boolean
    ): ApiResult<JsonObject>

    /**
     * Warning! this method will always return number of characters = n+1
     * gets a list of all children from the character node
     * @param idToken token to be used in Query ?auth=<idToken>
     */
    @GET(SUB_URL_CHARACTER + FIREBASE_JSON_SUFFIX)
    suspend fun getCharacterList(
            @Query(FIREBASE_AUTH_QUERY) idToken: String
    ): ApiResult<List<CharacterModel?>>

}