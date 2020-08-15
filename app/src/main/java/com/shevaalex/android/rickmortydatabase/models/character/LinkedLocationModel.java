package com.shevaalex.android.rickmortydatabase.models.character;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class LinkedLocationModel {
    private String name;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    @NonNull
    @Override
    public String toString() {
        return "LinkedLocationModel{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
