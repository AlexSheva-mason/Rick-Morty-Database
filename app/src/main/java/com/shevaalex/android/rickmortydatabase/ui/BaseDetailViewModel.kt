@file:Suppress("PropertyName")

package com.shevaalex.android.rickmortydatabase.ui

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.models.ApiObjectModel

abstract class BaseDetailViewModel<T: ApiObjectModel>: ViewModel() {

    protected val _detailObject = MutableLiveData<T>()
    val detailObject: LiveData<T> = _detailObject

    private val _motionStateId = MutableLiveData<Int>()
    val motionStateId: LiveData<Int> = _motionStateId

    fun setDetailObject(baseObject: T) {
        if (_detailObject.value != baseObject) {
            _detailObject.value = baseObject
        }
    }

    fun setMotionStateId(set: Int?) {
        set?.let {
            _motionStateId.value = it
        }
    }

}