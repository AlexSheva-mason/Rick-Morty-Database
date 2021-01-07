package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomnavigation.LabelVisibilityMode

class BottomNavViewModel : ViewModel() {

    private val _bottomNavVisibility = MutableLiveData<Int>()
    private val _bottomNavLabelVisibility = MutableLiveData<Int>()

    val bottomNavVisibility: LiveData<Int> = _bottomNavVisibility
    val bottomNavLabelVisibility: LiveData<Int> = _bottomNavLabelVisibility

    fun showBottomNav() {
        _bottomNavVisibility.postValue(View.VISIBLE)
    }

    fun hideBottomNav() {
        _bottomNavVisibility.postValue(View.GONE)
    }

    fun setLabelSelected() {
        _bottomNavLabelVisibility.postValue(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED)
    }

    fun setUnlabeled() {
        _bottomNavLabelVisibility.postValue(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED)
    }

}