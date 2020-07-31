package com.shevaalex.android.rickmortydatabase.utils;

import com.shevaalex.android.rickmortydatabase.source.database.Location;

public interface RecyclerViewAdapterCallback {
    Location returnLocationFromId(int locationId);
}
