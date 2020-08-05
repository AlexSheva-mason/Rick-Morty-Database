package com.shevaalex.android.rickmortydatabase.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class AppExecutors {
    private static final Object LOCK = new Object();
    private static volatile AppExecutors sInstance;
    private final ExecutorService diskIO;
    //scheduled executor service for retrofit (configuring timeout)
    private final ScheduledExecutorService mNetworkIO;

    private AppExecutors (ExecutorService diskIO, ScheduledExecutorService scExecutorService) {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.diskIO = diskIO;
        mNetworkIO = scExecutorService;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            Executors.newScheduledThreadPool(3));
                }
            }
        }
        return sInstance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    public ScheduledExecutorService networkIO() {
        return mNetworkIO;
    }
}
