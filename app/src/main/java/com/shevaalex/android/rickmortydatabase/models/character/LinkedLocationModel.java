package com.shevaalex.android.rickmortydatabase.models.character;

import androidx.annotation.NonNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkedLocationModel)) return false;
        LinkedLocationModel that = (LinkedLocationModel) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getUrl());
    }

}
