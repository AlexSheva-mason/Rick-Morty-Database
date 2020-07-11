package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CharacterSmall {
    @PrimaryKey
    private final int id;

    @ColumnInfo(collate = ColumnInfo.LOCALIZED)
    private String name;
    private String status;
    private String species;
    private String gender;
    private final int lastKnownLocation;
    private final String imgUrl;
    private final String episodeList;

    public CharacterSmall(int id, String name, String status, String species,
                          String gender, int lastKnownLocation, String imgUrl, String episodeList) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.gender = gender;
        this.lastKnownLocation = lastKnownLocation;
        this.imgUrl = imgUrl;
        this.episodeList = episodeList;
    }

    // GETTERS
    public int getId () { return id; }
    public String getName() {        return name;    }
    public String getStatus() {        return status;    }
    public String getSpecies() {        return species;    }
    public String getGender() {        return gender;    }
    public int getLastKnownLocation() {        return lastKnownLocation;    }
    public String getImgUrl() {        return imgUrl;    }
    public String getEpisodeList() {        return episodeList;    }


    //SETTERS
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CharacterSmall newChar = (CharacterSmall) obj;
        return getId() == newChar.getId() && getName().equals(newChar.getName());
    }
}
