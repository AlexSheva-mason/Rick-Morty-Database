package com.shevaalex.android.rickmortydatabase.utils;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;


public class StatusMediatorLiveData extends MediatorLiveData<Pair<Boolean, Boolean>> {
    public StatusMediatorLiveData(LiveData<Boolean> dbIsUpToDate, LiveData<Boolean> isConnected) {
        addSource(dbIsUpToDate, first -> {
            if (first != null && isConnected.getValue() != null) {
                setValue(Pair.create(first, isConnected.getValue()));
            }
        });
        addSource(isConnected, second -> {
            if (second != null && dbIsUpToDate.getValue() != null) {
                setValue(Pair.create(dbIsUpToDate.getValue(), second));
            }
        });
    }
}
