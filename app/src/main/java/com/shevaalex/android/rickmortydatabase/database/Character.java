package com.shevaalex.android.rickmortydatabase.database;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Character {

    @PrimaryKey
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private String originLocation;
    private String lastKnownLocation;
    private String imgUrl;
    private String episodeList;
    //TODO delete this?
    private String characterUrl;
    private String timeCreated;

    // TODO originLocation; lastKnownLocation; episodeList; make methods for parsing Strings and extracting only IDs
    public Character (int id, String name, String status, String species, String type,
                      String gender, String originLocation, String lastKnownLocation,
                      String imgUrl, String episodeList, String characterUrl, String timeCreated) {
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
        this.characterUrl = characterUrl;
        this.timeCreated = timeCreated;
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
    public String getCharacterUrl() {        return characterUrl;    }
    public String getTimeCreated() {        return timeCreated;    }

    // TODO delete setters?
    /*
    //SETTERS
    public void setId(int id) { this.id = id;    }
    public void setName(String name) {        this.name = name;    }
    public void setStatus(String status) {        this.status = status;    }
    public void setSpecies(String species) {        this.species = species;    }
    public void setType(String type) {        this.type = type;    }
    public void setGender(String gender) {        this.gender = gender;    }
    public void setImgUrl(String imgUrl) {        this.imgUrl = imgUrl;    }
    public void setCharacterUrl(String characterUrl) {        this.characterUrl = characterUrl;    }
    public void setTimeCreated(String timeCreated) {        this.timeCreated = timeCreated;    }
     */

}
