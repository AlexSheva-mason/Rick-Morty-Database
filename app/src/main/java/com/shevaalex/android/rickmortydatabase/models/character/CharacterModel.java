package com.shevaalex.android.rickmortydatabase.models.character;


import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel;
import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants.ApiCallCharacterKeys;


@SuppressWarnings("unused")
@Entity
public class CharacterModel extends ApiObjectModel {

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

    public CharacterModel(int id, String name, int timeStamp, String status, String species,
                          String gender, LinkedLocationModel originLocation,
                          LinkedLocationModel lastLocation, String imageUrl, String[] episodeList) {
        super(id, name, timeStamp);
        this.status = status;
        this.species = species;
        this.gender = gender;
        this.originLocation = originLocation;
        this.lastLocation = lastLocation;
        this.imageUrl = imageUrl;
        this.episodeList = episodeList;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterModel)) return false;
        if (!super.equals(o)) return false;
        CharacterModel that = (CharacterModel) o;
        return Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getSpecies(), that.getSpecies()) &&
                Objects.equals(getGender(), that.getGender()) &&
                Objects.equals(getOriginLocation(), that.getOriginLocation()) &&
                Objects.equals(getLastLocation(), that.getLastLocation()) &&
                Objects.equals(getImageUrl(), that.getImageUrl()) &&
                Arrays.equals(getEpisodeList(), that.getEpisodeList());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), getStatus(), getSpecies(), getGender(), getOriginLocation(), getLastLocation(), getImageUrl());
        result = 31 * result + Arrays.hashCode(getEpisodeList());
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "CharacterModel{" +
                "status='" + status + '\'' +
                ", species='" + species + '\'' +
                ", gender='" + gender + '\'' +
                ", originLocation=" + originLocation +
                ", lastLocation=" + lastLocation +
                ", imageUrl='" + imageUrl + '\'' +
                ", episodeList=" + Arrays.toString(episodeList) +
                "} " + super.toString();
    }
}
