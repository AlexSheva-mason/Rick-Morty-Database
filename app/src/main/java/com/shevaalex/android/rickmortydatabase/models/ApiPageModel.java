package com.shevaalex.android.rickmortydatabase.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;

public abstract class ApiPageModel {
    @SerializedName(ApiConstants.INFO)
    private ApiPageInfoModel apiPageInfoModel;

    public ApiPageInfoModel getApiPageInfoModel() {
        return apiPageInfoModel;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiPageModel{" +
                "apiPageInfoModel=" + apiPageInfoModel +
                '}';
    }
}
