package com.easynote.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.BackgroungColor
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.usecase.favoriteColors.DeleteFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.GetFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.SetFavoriteColorsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ColorPikerViewModel(
    val getFavoriteColorsUseCase: GetFavoriteColorsUseCase,
    val setFavoriteColorsUseCase: SetFavoriteColorsUseCase,
    val deleteFavoriteColorsUseCase: DeleteFavoriteColorsUseCase
) : ViewModel() {

    private val _favoriteColors = getFavoriteColorsUseCase.execute()
    val favoriteColors: LiveData<List<FavoriteColor>> get() = _favoriteColors

    fun setFavoriteColor(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedColor = if (position == 0) {
                currentColorsList.value!![0]
            } else {
                currentColorsList.value!![1]
            }
            var found = false
            val favList = favoriteColors.value
            favList!!.forEach { favColor ->
                if (favColor.number == selectedColor.colorWithHSL.color) {
                    found = true
                }
            }
            if (!found) {
                //цвета нет
                setFavoritesColors(
                    listOf(
                        FavoriteColor(
                            0,
                            selectedColor.colorWithHSL.color,
                            selectedColor.colorWithHSL.h,
                            selectedColor.colorWithHSL.s,
                            selectedColor.colorWithHSL.l
                        )
                    )
                )
            }
        }
    }

    fun setFavoritesColors(listFavColors: List<FavoriteColor>) {
        viewModelScope.launch(Dispatchers.IO) {
            setFavoriteColorsUseCase.execute(listFavColors)
        }
    }

    fun deleteFavoriteColor(favoriteColor: FavoriteColor) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteFavoriteColorsUseCase.execute(favoriteColor)
        }
    }

    private val _currentColorsList = MutableLiveData<List<BackgroungColor>>()
    val currentColorsList: LiveData<List<BackgroungColor>> get() = _currentColorsList

    fun setCurrentColors(listColors: List<BackgroungColor>) {
        _currentColorsList.value = listColors
    }

    fun setColorFromCurrentColorsList(color: BackgroungColor) {
        val currentColors = currentColorsList.value
        if (!currentColors.isNullOrEmpty()) {
            val currList = arrayListOf(currentColors[0], currentColors[1])
            currList[color.position] = color
            _currentColorsList.value = currList
        }
    }

    fun cleanSelectedColors(position: Int) {
        val editColors = currentColorsList.value
        if (!editColors.isNullOrEmpty()) {
            val currList = arrayListOf(editColors[0], editColors[1])
            currList[position] = BackgroungColor(position, ColorWithHSL(0, 512F, 256F, 256F))
            viewModelScope.launch(Dispatchers.IO) {
                _currentColorsList.postValue(currList)
            }
        }
    }

    private val _checkedColor = MutableLiveData<Boolean>()
    val selectorColor: LiveData<Boolean> = _checkedColor

    fun setCheckedColor(isCheck:Boolean){
        _checkedColor.value = isCheck
    }

}