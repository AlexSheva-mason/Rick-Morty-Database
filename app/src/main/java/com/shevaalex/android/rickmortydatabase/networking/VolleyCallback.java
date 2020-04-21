package com.shevaalex.android.rickmortydatabase.networking;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface VolleyCallback {
    void getJsonDataResponse(JSONObject response);
}
