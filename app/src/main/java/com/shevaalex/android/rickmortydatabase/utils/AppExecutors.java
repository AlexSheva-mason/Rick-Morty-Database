package com.shevaalex.android.rickmortydatabase.utils;


import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppExecutors {
    private static final Object LOCK = new Object();
    private static volatile AppExecutors sInstance;
    private final ExecutorService diskIO;
    private final Executor mainThreadExecutor;

    private AppExecutors (ExecutorService diskIO, Executor mainThreadExecutor) {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.diskIO = diskIO;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            new MainThreadExecutor());
                }
            }
        }
        return sInstance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    public Executor mainThreadExecutor() {
        return mainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable r) {
            handler.post(r);
        }
    }
}
