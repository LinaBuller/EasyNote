package com.easynote.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.easynote.domain.repository.SharedPrefRepository

class PermissionFragmentViewModel(var sharedPref: SharedPrefRepository) :
    BaseViewModel() {

    private val _permission = MutableLiveData<Boolean>()
    val permission: LiveData<Boolean> get() = _permission


    fun getPermissions(){

    }

}