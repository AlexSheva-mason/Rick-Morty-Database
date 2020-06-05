package com.shevaalex.android.rickmortydatabase.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppExecutors {
    private static final Object LOCK = new Object();
    private static volatile AppExecutors sInstance;
    private final ExecutorService diskIO;

    private AppExecutors (ExecutorService diskIO) {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.diskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppExecutors(Executors.newSingleThreadExecutor());
                }
            }
        }
        return sInstance;
    }

    public ExecutorService diskIO() {      return diskIO;    }
}
