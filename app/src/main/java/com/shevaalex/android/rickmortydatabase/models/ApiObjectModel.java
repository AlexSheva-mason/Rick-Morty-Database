package com.shevaalex.android.rickmortydatabase.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Objects;

public class ApiObjectModel {
    @PrimaryKey
    private int id;

    @ColumnInfo(collate = ColumnInfo.LOCALIZED)
    private String name;

    private int timeStamp;

    public ApiObjectModel(int id, String name, int timeStamp) {
        this.id = id;
        this.name = name;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiObjectModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiObjectModel)) return false;
        ApiObjectModel that = (ApiObjectModel) o;
        return getId() == that.getId() &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
