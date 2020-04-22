package com.shevaalex.android.rickmortydatabase.networking;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class VolleySingleton {

    private static final String LOG_TAG = VolleySingleton.class.getSimpleName();
    private static VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private final Context context;
    private static final Object LOCK = new Object();
    private static final String TAG = "DbRequest";

    private VolleySingleton (Context context) {
        this.context = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    // returns a single instance of a class
    static synchronized VolleySingleton getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating a new Volley instance");
                sInstance = new VolleySingleton(context); }
        } else {
            Log.d(LOG_TAG, "Returning previous Volley instance");
        }
        return sInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    void cancelRequests () {
        Log.d(LOG_TAG, "Cancelling requests");
        mRequestQueue.cancelAll(TAG);
    }

    /* to add a specific request (eg. jsonObjectRequest)
        Adds the specified request to the global queue
     */
    <T> void addToRequestQueue (Request<T> request){
        Log.d(LOG_TAG, "Adding request to queue: " + request.getUrl());
        request.setTag(TAG);
        getRequestQueue().add(request);
    }
}
