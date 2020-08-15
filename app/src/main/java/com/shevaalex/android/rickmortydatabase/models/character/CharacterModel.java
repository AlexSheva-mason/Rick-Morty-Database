package com.shevaalex.android.rickmortydatabase.models.character;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

import com.shevaalex.android.rickmortydatabase.source.network.net_utils.ApiConstants.ApiCallCharacterKeys;


//TODO rename to Character.java (and later on to something different to prefent clashing with Character)
// and replace all imports from com.shevaalex.android.rickmortydatabase.source.database.character to this one ^^^
@SuppressWarnings("unused")
@Entity
public class CharacterModel {
    @PrimaryKey
    private final int id;

    @ColumnInfo(collate = ColumnInfo.LOCALIZED)
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

    private int timeStamp;

    public CharacterModel(int id, String name, String status, String species, String gender,
                          LinkedLocationModel originLocation, LinkedLocationModel lastLocation,
                          String imageUrl, String[] episodeList, int timeStamp) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.species = species;
        this.gender = gender;
        this.originLocation = originLocation;
        this.lastLocation = lastLocation;
        this.imageUrl = imageUrl;
        this.episodeList = episodeList;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
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

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterModel that = (CharacterModel) o;
        return id == that.id &&
                timeStamp == that.timeStamp &&
                Objects.equals(name, that.name) &&
                Objects.equals(status, that.status) &&
                Objects.equals(species, that.species) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(originLocation, that.originLocation) &&
                Objects.equals(lastLocation, that.lastLocation) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Arrays.equals(episodeList, that.episodeList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, status, species, gender,
                originLocation, lastLocation, imageUrl, timeStamp);
        result = 31 * result + Arrays.hashCode(episodeList);
        return result;
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
