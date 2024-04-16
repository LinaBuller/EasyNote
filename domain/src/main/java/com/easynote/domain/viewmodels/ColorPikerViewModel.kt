package com.easynote.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.BackgroundColor
import com.easynote.domain.models.ColorWithHSL
import com.easynote.domain.models.CurrentTheme
import com.easynote.domain.models.FavoriteColor
import com.easynote.domain.usecase.favoriteColors.DeleteFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.GetFavoriteColorsUseCase
import com.easynote.domain.usecase.favoriteColors.SetFavoriteColorsUseCase
import com.example.domain.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ColorPikerViewModel(
    val getFavoriteColorsUseCase: GetFavoriteColorsUseCase,
    val setFavoriteColorsUseCase: SetFavoriteColorsUseCase,
    val deleteFavoriteColorsUseCase: DeleteFavoriteColorsUseCase
) : BaseViewModel() {

    private val _favoriteColors = getFavoriteColorsUseCase.execute()
    val favoriteColors: LiveData<List<FavoriteColor>> get() = _favoriteColors

    fun setFavoriteColor(position: Int) {

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
            setMessage(R.string.add_fav_color)
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
        } else {
            setMessage(R.string.exist_fav_color)
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

    private val _currentColorsList = MutableLiveData<List<BackgroundColor>>()
    val currentColorsList: LiveData<List<BackgroundColor>> get() = _currentColorsList

    fun setCurrentColors(listColors: List<BackgroundColor>) {
        _currentColorsList.value = listColors
    }

    fun setColorFromCurrentColorsList(color: BackgroundColor) {
        val currentColors = currentColorsList.value
        if (!currentColors.isNullOrEmpty()) {
            val currList = arrayListOf(currentColors[0], currentColors[1])
            currList[color.position] = color
            _currentColorsList.value = currList
        }
    }

    fun cleanSelectedColors(theme: CurrentTheme, position: Int) {
        val editColors = currentColorsList.value
        if (!editColors.isNullOrEmpty()) {
            val currList = arrayListOf(editColors[0], editColors[1])
            if (theme.themeId == 0) {
                currList[position] = BackgroundColor(position, WHITE_COLOR_BACKGROUND)
            } else {
                //depend of main color dark theme <color name="element_dark">#1F1B24</color>
                currList[position] = BackgroundColor(position, BLACK_COLOR_BACKGROUND)
            }


            viewModelScope.launch(Dispatchers.IO) {
                _currentColorsList.postValue(currList)
            }
        }
    }

    private val _checkedColor = MutableLiveData<Boolean>()
    val checkedColor: LiveData<Boolean> = _checkedColor

    fun setCheckedColor(isCheck: Boolean) {
        _checkedColor.value = isCheck
    }

    companion object {

        val WHITE_COLOR_BACKGROUND = ColorWithHSL(-1, 0F, 0F, 1F)
        val BLACK_COLOR_BACKGROUND = ColorWithHSL(267, 0.3738F, 0.3204F, 1F)

    }
}