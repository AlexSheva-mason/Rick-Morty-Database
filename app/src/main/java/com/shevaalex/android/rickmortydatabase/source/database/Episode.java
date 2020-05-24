package com.shevaalex.android.rickmortydatabase.source.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Episode {
    @PrimaryKey
    private final int id;
    private final String name;
    private final String airDate;
    private final String code;
    private final String charactersList;

    public Episode (int id, String name, String airDate, String code, String charactersList) {
        this.id = id;
        this.name = name;
        this.airDate = airDate;
        this.code = code;
        this.charactersList = charactersList;
    }

    public int getId() {        return id;    }

    public String getName() {        return name;    }

    public String getAirDate() {        return airDate;    }

    public String getCode() {        return code;    }

    String getCharactersList() {        return charactersList;    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Episode newEpisode = (Episode) obj;
        return getId() == newEpisode.getId() && getName().equals(newEpisode.getName()) && getAirDate().equals(newEpisode.getAirDate())
                && getCode().equals(newEpisode.getCode()) && getCharactersList().equals(newEpisode.getCharactersList());
    }

}
