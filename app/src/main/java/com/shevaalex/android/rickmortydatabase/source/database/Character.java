package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class Character extends CharacterSmall {

    @ColumnInfo(collate = ColumnInfo.LOCALIZED)
    private final String type;
    private final int originLocation;

    public Character (int id, String name, String status, String species, String type,
                      String gender, int originLocation, int lastKnownLocation,
                      String imgUrl, String episodeList) {
        super(id, name, status, species, gender, lastKnownLocation, imgUrl, episodeList);
        this.type = type;
        this.originLocation = originLocation;
    }

    // GETTERS
    public String getType() {        return type;    }
    public int getOriginLocation() {        return originLocation;    }


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
