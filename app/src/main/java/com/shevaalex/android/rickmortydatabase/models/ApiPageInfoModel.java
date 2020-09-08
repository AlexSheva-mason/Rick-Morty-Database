package com.shevaalex.android.rickmortydatabase.models;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class ApiPageInfoModel {
    private int count;
    private int pages;

    public int getCount() {
        return count;
    }

    public int getPages() {
        return pages;
    }

    @NonNull
    @Override
    public String toString() {
        return "CharPageInfoModel{" +
                "count=" + count +
                ", pages=" + pages +
                '}';
    }
}
