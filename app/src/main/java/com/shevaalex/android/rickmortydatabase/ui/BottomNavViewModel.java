package com.shevaalex.android.rickmortydatabase.ui;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;


public class BottomNavViewModel extends ViewModel {
    private MutableLiveData<Integer> bottomNavVisibility;
    private MutableLiveData<Integer> bottomNavLabelVisibility;

    public LiveData<Integer> getBottomNavVisibility() {
        if (bottomNavVisibility == null) {
            bottomNavVisibility = new MutableLiveData<>();
            showBottomNav();
        }
        return bottomNavVisibility;
    }

    public LiveData<Integer> getBottomNavLabelStatus() {
        if (bottomNavLabelVisibility == null) {
            bottomNavLabelVisibility = new MutableLiveData<>();
            setLabelSelected();
        }
        return bottomNavLabelVisibility;
    }

    public void showBottomNav() {
        bottomNavVisibility.postValue(View.VISIBLE);
    }

    public void hideBottomNav() {
        bottomNavVisibility.postValue(View.GONE);
    }

    public void setLabelSelected() {
        bottomNavLabelVisibility.postValue(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED);
    }

    public void setUnlabeled() {
        bottomNavLabelVisibility.postValue(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);
    }
}