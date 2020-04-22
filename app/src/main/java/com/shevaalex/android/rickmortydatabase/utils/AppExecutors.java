package com.shevaalex.android.rickmortydatabase.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AppExecutors {
    private static final String LOG_TAG = AppExecutors.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final ExecutorService diskIO;
    private final Executor networkIO;
    private final Executor mainThreadExecutor;

    private AppExecutors (ExecutorService diskIO, Executor networkIO, Executor mainThreadExecutor) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        // TODO possible will not be needed later
        this.mainThreadExecutor = mainThreadExecutor;
    }

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating a new AppExecutors instance");
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), new MainThreadExecutor());
            }
        } else {
            Log.d(LOG_TAG, "Getting a previous AppExecutors instance");
        }
        return sInstance;
    }

    public ExecutorService diskIO() {      return diskIO;    }
    public Executor networkIO() {        return networkIO;    }
    public Executor MainThreadExecutor() {        return mainThreadExecutor;    }

    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable r) {
            handler.post(r);
        }
    }
}
