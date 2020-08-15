package com.shevaalex.android.rickmortydatabase.models.character;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;

@SuppressWarnings("unused")
public class CharacterPageModel {
    @SerializedName(ApiConstants.INFO)
    private CharPageInfoModel charPageInfoModel;

    @SerializedName(ApiConstants.RESULTS_ARRAY)
    private List<CharacterModel> characterModels = new ArrayList<>();

    public CharPageInfoModel getCharPageInfoModel() {
        return charPageInfoModel;
    }

    public List<CharacterModel> getCharacterModels() {
        return characterModels;
    }

    @NonNull
    @Override
    public String toString() {
        return "CharacterPageModel{" +
                "characterPageInfoModel=" + charPageInfoModel +
                ", characterModels=" + characterModels +
                '}';
    }

}
