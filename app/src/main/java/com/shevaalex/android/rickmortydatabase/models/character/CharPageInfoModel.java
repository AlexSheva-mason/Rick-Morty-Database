package com.shevaalex.android.rickmortydatabase.models.character;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants;

@SuppressWarnings("unused")
public class CharPageInfoModel {
    private int count;
    private int pages;
    @SerializedName(ApiConstants.KEY_PAGE_NEXT)
    private String nextPage;
    @SerializedName(ApiConstants.KEY_PAGE_PREV)
    private String previousPage;

    public int getCount() {
        return count;
    }

    public int getPages() {
        return pages;
    }

    public String getNextPage() {
        return nextPage;
    }

    public String getPreviousPage() {
        return previousPage;
    }

    @NonNull
    @Override
    public String toString() {
        return "CharPageInfoModel{" +
                "count=" + count +
                ", pages=" + pages +
                ", nextPage='" + nextPage + '\'' +
                ", previousPage='" + previousPage + '\'' +
                '}';
    }
}
