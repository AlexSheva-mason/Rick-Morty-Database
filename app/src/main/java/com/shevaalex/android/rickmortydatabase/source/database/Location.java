package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey
    private final int id;
    private String name;
    private String type;
    private String dimension;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Location newLoc = (Location) obj;
        return getId() == newLoc.getId() && getName().equals(newLoc.getName()) && getType().equals(newLoc.getType())
                && getDimension().equals(newLoc.getDimension()) && getResidentsList().equals(newLoc.getResidentsList());
    }
}
