@file:Suppress("PropertyName")

package com.shevaalex.android.rickmortydatabase.ui.viewmodel

import androidx.lifecycle.*
import com.shevaalex.android.rickmortydatabase.models.RmObject

abstract class BaseDetailViewModel<T: RmObject>: ViewModel() {

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