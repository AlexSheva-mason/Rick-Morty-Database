package com.shevaalex.android.rickmortydatabase.source.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity (primaryKeys = {"characterId", "episodeId"},
            foreignKeys = {
                        @ForeignKey(entity = Character.class,
                            parentColumns = "id",
                            childColumns = "characterId"),
                        @ForeignKey(entity = Episode.class,
                            parentColumns = "id",
                            childColumns = "episodeId")})

public class CharacterEpisodeJoin {
    private final int characterId;
    private final int episodeId;


    public CharacterEpisodeJoin(int characterId, int episodeId) {
        this.characterId = characterId;
        this.episodeId = episodeId;
    }

    int getCharacterId() {        return characterId;    }

    int getEpisodeId() {        return episodeId;    }
}
