package com.shevaalex.android.rickmortydatabase.utils;

import android.util.Log;

public abstract class StringParsing {
    public static int parseEpisodeIds(String episodeUrl) {
        int idIndex = episodeUrl.lastIndexOf("/") + 1;
        return Integer.parseInt(episodeUrl.substring(idIndex));
    }
}
