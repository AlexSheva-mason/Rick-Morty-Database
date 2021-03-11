package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.common.truth.Truth.assertThat
import com.shevaalex.android.rickmortydatabase.getOrAwaitValueTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BottomNavViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BottomNavViewModel

    @Before
    fun setUp() {
        viewModel = BottomNavViewModel()
    }

    @Test
    fun showBottomNavShouldPostValueVisible() {
        viewModel.showBottomNav()
        val result = viewModel.bottomNavVisibility.getOrAwaitValueTest()
        assertThat(result).isEqualTo(View.VISIBLE)
    }

    @Test
    fun hideBottomNavShouldPostValueGone() {
        viewModel.hideBottomNav()
        val result = viewModel.bottomNavVisibility.getOrAwaitValueTest()
        assertThat(result).isEqualTo(View.GONE)
    }

    @Test
    fun setLabelSelectedSetsLabelVisibilitySelected() {
        viewModel.setLabelSelected()
        val result = viewModel.bottomNavLabelVisibility.getOrAwaitValueTest()
        assertThat(result).isEqualTo(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED)
    }

    @Test
    fun setUnlabeledSetsLabelVisibilityUnlabeled() {
        viewModel.setUnlabeled()
        val result = viewModel.bottomNavLabelVisibility.getOrAwaitValueTest()
        assertThat(result).isEqualTo(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED)
    }

}