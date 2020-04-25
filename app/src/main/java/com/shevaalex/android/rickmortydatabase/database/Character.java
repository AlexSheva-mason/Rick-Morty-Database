package com.shevaalex.android.rickmortydatabase.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Character {

    @PrimaryKey
    private final int id;
    private final String name;
    private final String status;
    private final String species;
    private final String type;
    private final String gender;
    private final String originLocation;
    private final String lastKnownLocation;
    private final String imgUrl;
    private final String episodeList;

    // TODO originLocation; lastKnownLocation; episodeList; make methods for parsing Strings and extracting only IDs
    public Character (int id, String name, String status, String species, String type,
                      String gender, String originLocation, String lastKnownLocation,
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
    public String getOriginLocation() {        return originLocation;    }
    public String getLastKnownLocation() {        return lastKnownLocation;    }
    public String getImgUrl() {        return imgUrl;    }
    public String getEpisodeList() {        return episodeList;    }

}
