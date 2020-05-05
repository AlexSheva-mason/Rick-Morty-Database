package com.shevaalex.android.rickmortydatabase.utils;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppExecutors {
    private static final String LOG_TAG = AppExecutors.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final ExecutorService diskIO;
    private final Executor networkIO;

    private AppExecutors (ExecutorService diskIO, Executor networkIO) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating a new AppExecutors instance");
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3));
            }
        } else {
            Log.d(LOG_TAG, "Getting a previous AppExecutors instance");
        }
        return sInstance;
    }

    public ExecutorService diskIO() {      return diskIO;    }
    public Executor networkIO() {        return networkIO;    }
}
