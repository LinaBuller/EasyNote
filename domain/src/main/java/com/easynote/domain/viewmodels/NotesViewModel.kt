package com.easynote.domain.viewmodels


import androidx.lifecycle.*
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.usecase.sharedPreferenses.GetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.GetPreferredThemeUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetIsFirstUsagesUseCase
import com.easynote.domain.usecase.sharedPreferenses.SetPreferredThemeUseCase


class NotesViewModel(
    val getIsFirstUsagesUseCase: GetIsFirstUsagesUseCase,
    val setIsFirstUsagesUseCase: SetIsFirstUsagesUseCase,
    val getPreferredThemeUseCase: GetPreferredThemeUseCase,
    val setPreferredThemeUseCase: SetPreferredThemeUseCase,
) : BaseViewModel(){

    private val _currentTheme = MutableLiveData(CurrentTheme(0))
    val currentTheme: LiveData<CurrentTheme> = _currentTheme

    fun changeTheme(id: Int) {
        val currentThemeNow = _currentTheme.value
        if (currentThemeNow != null) {
            _currentTheme.value = currentThemeNow.copy(themeId = id)
        }
        if (id == 0) {
            setPreferredThemeSharedPref(true)
        } else {
            setPreferredThemeSharedPref(false)
        }
    }


    var isFirstUsages: Boolean = true
    var preferredTheme: Boolean = true

    fun getIsFirstUsagesSharedPref() {
        isFirstUsages = getIsFirstUsagesUseCase.execute()
    }

    fun setIsFirstUsagesSharPref(isFirst: Boolean) {
        isFirstUsages = isFirst
        setIsFirstUsagesUseCase.execute(isFirst)
    }

    fun getPreferredThemeSharedPref() {
        preferredTheme = getPreferredThemeUseCase.execute()
    }

    private fun setPreferredThemeSharedPref(prefTheme: Boolean) {
        preferredTheme = prefTheme
        setPreferredThemeUseCase.execute(prefTheme)
    }

}



