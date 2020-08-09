package com.shevaalex.android.rickmortydatabase.models.character;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import com.shevaalex.android.rickmortydatabase.source.network.ApiConstants.ApiCallCharacterKeys;

@SuppressWarnings("unused")
public class CharacterModel {
    private int id;
    private String name;
    private String status;
    private String species;
    private String gender;

    @SerializedName(ApiCallCharacterKeys.CHARACTER_ORIGIN_LOCATION)
    private LinkedLocationModel originLocation;

    @SerializedName(ApiCallCharacterKeys.CHARACTER_LAST_LOCATION)
    private LinkedLocationModel lastLocation;

    @SerializedName(ApiCallCharacterKeys.CHARACTER_IMAGE_URL)
    private String imageUrl;

    @SerializedName(ApiCallCharacterKeys.CHARACTER_EPISODE_LIST)
    private String[] episodeList;

    //TODO add character url?
    //private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LinkedLocationModel getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(LinkedLocationModel originLocation) {
        this.originLocation = originLocation;
    }

    public LinkedLocationModel getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(LinkedLocationModel lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String[] getEpisodeList() {
        return episodeList;
    }

    public void setEpisodeList(String[] episodeList) {
        this.episodeList = episodeList;
    }

    @NonNull
    @Override
    public String toString() {
        return "CharacterModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", species='" + species + '\'' +
                ", gender='" + gender + '\'' +
                ", originLocation=" + originLocation +
                ", lastLocation=" + lastLocation +
                ", imageUrl='" + imageUrl + '\'' +
                ", episodeList=" + Arrays.toString(episodeList) +
                '}';
    }
}
