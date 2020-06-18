package com.shevaalex.android.rickmortydatabase.source.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class VolleySingleton {
    private static volatile VolleySingleton sInstance;
    private RequestQueue mRequestQueue;
    private final Context context;
    private static final Object LOCK = new Object();
    private static final String TAG = "DbRequest";

    private VolleySingleton (Context context) {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.context = context.getApplicationContext();
        mRequestQueue = getRequestQueue();
    }

    // returns a single instance of a class
    static synchronized VolleySingleton getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new VolleySingleton(context);
                }
            }
        }
        return sInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    void cancelRequests (String urlKey) {
        mRequestQueue.cancelAll(TAG);
        getRequestQueue().getCache().invalidate(urlKey, true);
    }

    /* to add a specific request (eg. jsonObjectRequest)
        Adds the specified request to the global queue
     */
    <T> void addToRequestQueue (Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }
}
