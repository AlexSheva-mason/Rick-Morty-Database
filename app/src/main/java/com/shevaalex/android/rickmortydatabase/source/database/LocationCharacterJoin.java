package com.shevaalex.android.rickmortydatabase.source.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity (indices = {@Index("locationId")},
            primaryKeys = {"characterId", "locationId"},
            foreignKeys = {
                        @ForeignKey(entity = Character.class,
                            parentColumns = "id",
                            childColumns = "characterId"),
                        @ForeignKey(entity = Location.class,
                            parentColumns = "id",
                            childColumns = "locationId")})

public class LocationCharacterJoin {
    private final int characterId;
    private final int locationId;


    public LocationCharacterJoin(int characterId, int locationId) {
        this.characterId = characterId;
        this.locationId = locationId;
    }

    int getCharacterId() {        return characterId;    }

    int getLocationId() {        return locationId;    }
}
