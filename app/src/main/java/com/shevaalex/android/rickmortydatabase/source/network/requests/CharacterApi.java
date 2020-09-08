package com.shevaalex.android.rickmortydatabase.source.network.requests;

import androidx.lifecycle.LiveData;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.source.network.ApiResponse;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CharacterApi {
    //gets page with Characters
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER)
    LiveData<ApiResponse<CharacterPageModel>> getCharactersPage
    (@Query(ApiConstants.KEY_QUERY_PAGE) String page);

    //gets a single Character
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER + "{id}")
    LiveData<ApiResponse<CharacterModel>> getCharacter(@Path("id") int charId);
}
