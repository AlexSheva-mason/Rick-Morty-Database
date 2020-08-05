package com.shevaalex.android.rickmortydatabase.source.network.requests;

import com.shevaalex.android.rickmortydatabase.models.character.CharacterModel;
import com.shevaalex.android.rickmortydatabase.source.network.ApiConstants;
import com.shevaalex.android.rickmortydatabase.models.character.CharacterPageModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CharacterApi {
    //gets page with Characters
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER)
    Call<CharacterPageModel> getCharactersPage(@Query(ApiConstants.KEY_QUERY_PAGE) String page);

    //gets single Character
    @GET(ApiConstants.ApiCallCharacterKeys.SUB_URL_CHARACTER + "{id}")
    Call<CharacterModel> getCharacter(@Path("id") int charId);
}
