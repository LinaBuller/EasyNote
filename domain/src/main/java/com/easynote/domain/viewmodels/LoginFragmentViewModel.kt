package com.easynote.domain.viewmodels

import androidx.lifecycle.ViewModel
import com.easynote.domain.usecase.sharedPreferenses.GetIsBioAuthUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetIsBioAuthUseCase

class LoginFragmentViewModel(
    private val getIsBioAuthUseCase: GetIsBioAuthUseCase,
    private val setIsBioAuthUseCase: SetIsBioAuthUseCase
) : ViewModel() {

    var isBioAuth = false

    fun getIsBioAuthSharedPref() {
        isBioAuth = getIsBioAuthUseCase.execute()
    }

    fun setIsBioAuthSharedPref(newIsBioAuth:Boolean){
        isBioAuth = newIsBioAuth
        setIsBioAuthUseCase.execute(isBioAuth)
    }

}