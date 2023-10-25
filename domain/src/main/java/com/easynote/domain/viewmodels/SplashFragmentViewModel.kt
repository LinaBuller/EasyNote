package com.easynote.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.easynote.domain.repository.SharedPrefRepository

class SplashFragmentViewModel(private val sharedPrefRepository: SharedPrefRepository) : BaseViewModel() {

    private val _isFirstUse = MutableLiveData<Boolean>()
    val isFirstUse:LiveData<Boolean> get() = _isFirstUse

    fun getFirstUsages(){
        _isFirstUse.value = sharedPrefRepository.getFirstUsages()
    }
}