package com.shevaalex.android.rickmortydatabase.source.remote

import com.shevaalex.android.rickmortydatabase.models.location.LocationModel
import com.shevaalex.android.rickmortydatabase.models.location.LocationPageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LocationApi {

    //gets a page with Locations
    @GET(ApiConstants.ApiCallLocationKeys.SUB_URL_LOCATION)
    suspend fun getLocationsPage(
            @Query(ApiConstants.KEY_QUERY_PAGE) page: String
    ): ApiResult<LocationPageModel>

    //gets a single Location object
    @GET(ApiConstants.ApiCallLocationKeys.SUB_URL_LOCATION + "{id}")
    suspend fun getLocation (
            @Path("id") locationId: Int
    ): ApiResult<LocationModel>

}