package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CharacterSmall {

    @PrimaryKey
    private final int id;
    private final String name;
    private final String status;
    private final String species;
    private final String gender;
    private final int lastKnownLocation;
    private final String imgUrl;


    public CharacterSmall(int id, String name, String status, String species, String gender, int lastKnownLocation, String imgUrl) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.gender = gender;
        this.lastKnownLocation = lastKnownLocation;
        this.imgUrl = imgUrl;
    }


    // GETTERS
    public int getId () { return id; }
    public String getName() {        return name;    }
    public String getStatus() {        return status;    }
    public String getSpecies() {        return species;    }
    public String getGender() {        return gender;    }
    public int getLastKnownLocation() {        return lastKnownLocation;    }
    public String getImgUrl() {        return imgUrl;    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CharacterSmall newChar = (CharacterSmall) obj;
        return getId() == newChar.getId() && getName().equals(newChar.getName()) && getStatus().equals(newChar.getStatus()) && getSpecies().equals(newChar.getSpecies())
                && getGender().equals(newChar.getGender()) && getLastKnownLocation() == newChar.getLastKnownLocation()
                && getImgUrl().equals(newChar.getImgUrl());
    }
}
