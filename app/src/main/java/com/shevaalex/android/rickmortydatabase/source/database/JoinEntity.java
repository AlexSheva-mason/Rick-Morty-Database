package com.shevaalex.android.rickmortydatabase.source.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity (primaryKeys = {"characterId", "locationId", "episodeId"},
            foreignKeys = {
                        @ForeignKey(entity = Character.class,
                            parentColumns = "id",
                            childColumns = "characterId"),
                        @ForeignKey(entity = Location.class,
                            parentColumns = "id",
                            childColumns = "locationId"),
                        @ForeignKey(entity = Episode.class,
                            parentColumns = "id",
                            childColumns = "episodeId")})

public class JoinEntity {
    private final int characterId;
    private final int locationId;
    private final int episodeId;


    public JoinEntity(int characterId, int locationId, int episodeId) {
        this.characterId = characterId;
        this.locationId = locationId;
        this.episodeId = episodeId;
    }

    public int getCharacterId() {        return characterId;    }

    public int getLocationId() {        return locationId;    }

    public int getEpisodeId() {        return episodeId;    }
}
