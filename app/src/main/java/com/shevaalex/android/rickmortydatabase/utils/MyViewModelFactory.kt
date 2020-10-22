package com.shevaalex.android.rickmortydatabase.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class MyViewModelFactory<VM : ViewModel> @Inject constructor(
    private val viewModelProvider: @JvmSuppressWildcards Provider<VM>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
     override fun <T : ViewModel?> create(modelClass: Class<T>): T = 
         viewModelProvider.get() as T

}