package com.easynote.domain.viewmodels


import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.easynote.domain.utils.Event

open class BaseViewModel : ViewModel() {
    private val _message = MutableLiveData<Event<Int>>()
    val message: LiveData<Event<Int>> get() = _message

    // Post in background thread
    fun postMessage(@StringRes message: Int) {
        _message.postValue(Event(message))
    }

    // Post in main thread
    fun setMessage(@StringRes message: Int) {
        _message.value = Event(message)
    }

    private val _progress = MutableLiveData<Event<Boolean>>()
    val progress: LiveData<Event<Boolean>> get() = _progress


    fun setVisibleProgressBar(isVisible:Boolean){
        _progress.value = Event(isVisible)
    }
    fun postVisibleProgressBar(isVisible:Boolean){
        _progress.postValue(Event(isVisible))
    }
}