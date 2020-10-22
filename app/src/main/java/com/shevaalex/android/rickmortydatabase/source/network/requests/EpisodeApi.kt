package com.shevaalex.android.rickmortydatabase.source.network.requests

import com.shevaalex.android.rickmortydatabase.models.episode.EpisodeModel
import com.shevaalex.android.rickmortydatabase.models.episode.EpisodePageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EpisodeApi {

    //gets a page with Episodes
    @GET(ApiConstants.ApiCallEpisodeKeys.SUB_URL_EPISODE)
    suspend fun getEpisodesPage(
            @Query(ApiConstants.KEY_QUERY_PAGE) page: String
    ): ApiResult<EpisodePageModel>

    //gets a single Episode object
    @GET(ApiConstants.ApiCallEpisodeKeys.SUB_URL_EPISODE + "{id}")
    suspend fun getEpisode (
            @Path("id") episodeId: Int
    ): ApiResult<EpisodeModel>

}