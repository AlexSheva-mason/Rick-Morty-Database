package com.shevaalex.android.rickmortydatabase.source.remote

import com.google.gson.JsonObject
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.utils.networking.*
import retrofit2.http.GET
import retrofit2.http.Query

interface EpisodeApi {

    /**
     * gets a list of all children from the episode node
     * @param idToken token to be used in Query ?auth=<idToken>
     * @param isShallow boolean is used in Query &shallow=isShallow. Pass true to get shallow result
     */
    @GET(SUB_URL_EPISODE + FIREBASE_JSON_SUFFIX)
    suspend fun getEpisodeList(
            @Query(FIREBASE_AUTH_QUERY) idToken: String,
            @Query(FIREBASE_SHALLOW_QUERY) isShallow: Boolean
    ): ApiResult<JsonObject>

    /**
     * Warning! this method will always return number of episodes = n+1
     * gets a list of all children from the episode node
     * @param idToken token to be used in Query ?auth=<idToken>
     */
    @GET(SUB_URL_EPISODE + FIREBASE_JSON_SUFFIX)
    suspend fun getEpisodeList(
            @Query(FIREBASE_AUTH_QUERY) idToken: String
    ): ApiResult<List<EpisodeModel?>>

}