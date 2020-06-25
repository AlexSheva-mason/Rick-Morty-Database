package com.shevaalex.android.rickmortydatabase.source.database;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;

@Entity (indices = {@Index("episodeId")},
        primaryKeys = {"characterId", "episodeId"},
        foreignKeys = {
                        @ForeignKey(onDelete = CASCADE, entity = Character.class,
                            parentColumns = "id",
                            childColumns = "characterId"),
                        @ForeignKey(onDelete = CASCADE, entity = Episode.class,
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
