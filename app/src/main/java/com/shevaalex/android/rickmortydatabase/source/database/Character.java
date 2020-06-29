package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Character {

    @PrimaryKey
    private final int id;

    @ColumnInfo(collate = ColumnInfo.LOCALIZED)
    private final String name;
    private final String status;
    private final String species;
    private final String type;
    private final String gender;
    private final int originLocation;
    private final int lastKnownLocation;
    private final String imgUrl;
    private final String episodeList;


    public Character (int id, String name, String status, String species, String type,
                      String gender, int originLocation, int lastKnownLocation,
                      String imgUrl, String episodeList) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.type = type;
        this.gender = gender;
        this.originLocation = originLocation;
        this.lastKnownLocation = lastKnownLocation;
        this.imgUrl = imgUrl;
        this.episodeList = episodeList;
    }


    // GETTERS
    public int getId () { return id; }
    public String getName() {        return name;    }
    public String getStatus() {        return status;    }
    public String getSpecies() {        return species;    }
    public String getType() {        return type;    }
    public String getGender() {        return gender;    }
    public int getOriginLocation() {        return originLocation;    }
    public int getLastKnownLocation() {        return lastKnownLocation;    }
    public String getImgUrl() {        return imgUrl;    }
    public String getEpisodeList() {        return episodeList;    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Character newChar = (Character) obj;
        return getId() == newChar.getId() && getName().equals(newChar.getName()) && getStatus().equals(newChar.getStatus()) && getSpecies().equals(newChar.getSpecies())
                && getType().equals(newChar.getType()) && getGender().equals(newChar.getGender())
                && getOriginLocation() == newChar.getOriginLocation() && getLastKnownLocation() == newChar.getLastKnownLocation()
                && getImgUrl().equals(newChar.getImgUrl());
    }
}
