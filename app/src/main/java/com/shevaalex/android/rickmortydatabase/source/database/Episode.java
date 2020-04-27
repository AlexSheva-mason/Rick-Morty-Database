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

    public String getCharactersList() {        return charactersList;    }
}
