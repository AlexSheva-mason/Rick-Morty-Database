package com.shevaalex.android.rickmortydatabase.source.network.requests

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiConstants
import com.shevaalex.android.rickmortydatabase.utils.networking.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CharacterApi {

    //gets page with Characters
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER)
    suspend fun getCharactersPage(
            @Query(ApiConstants.KEY_QUERY_PAGE) page: String
    ): ApiResult<CharacterPageModel>

    //gets a single Character object
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER + "{id}")
    suspend fun getCharacter(
            @Path("id") charId: Int
    ): ApiResult<CharacterModel>

}