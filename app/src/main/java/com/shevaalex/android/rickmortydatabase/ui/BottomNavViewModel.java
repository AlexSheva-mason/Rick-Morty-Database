package com.shevaalex.android.rickmortydatabase.ui;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class BottomNavViewModel extends ViewModel {
    private MutableLiveData<Integer> bottomNavVisibility;

    public LiveData<Integer> getBottomNavVisibility() {
        if (bottomNavVisibility == null) {
            bottomNavVisibility = new MutableLiveData<>();
            showBottomNav();
        }
        return bottomNavVisibility;
    }

    public void showBottomNav() {
        bottomNavVisibility.postValue(View.VISIBLE);
    }

    public void hideBottomNav() {
        bottomNavVisibility.postValue(View.GONE);
    }
}