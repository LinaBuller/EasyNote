package com.buller.mysqlite.fragments.add.bottomsheet.pickerFavoriteColor

import com.easynote.domain.models.FavoriteColor

interface OnChangeColorsFields {
    fun onChangeEditedColorFromCheckbox(colorToField: FavoriteColor, holder: FavoriteColorAdapter.FavoriteColorHolder)
    fun onDeleteFavColor(favoriteColor:FavoriteColor)
    fun onAddNewFavColor()
}