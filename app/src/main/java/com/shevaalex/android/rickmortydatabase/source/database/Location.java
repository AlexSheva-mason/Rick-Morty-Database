package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey
    private final int id;
    private final String name;
    private final String type;
    private final String dimension;
    private final String residentsList;

    public Location (int id, String name, String type, String dimension, String residentsList) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dimension = dimension;
        this.residentsList = residentsList;
    }

    public int getId() {        return id;    }

    public String getName() {        return name;    }

    public String getType() {        return type;    }

    public String getDimension() {        return dimension;    }

    public String getResidentsList() {        return residentsList;    }
}
