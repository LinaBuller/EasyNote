package com.easynote.domain.utils.theme

import android.content.Context
import com.dolatkia.animatedThemeManager.AppTheme

interface BaseTheme: AppTheme {
    fun backgroundColor(context:Context):Int
    fun backgroundColorEditText(context:Context):Int
    fun backgroundDrawer(context:Context):Int
    fun backgroundBottomDrawer(context:Context):Int
    fun textColor(context:Context):Int
    fun textColorTabSelect(context: Context):Int
    fun textColorTabUnselect(context: Context):Int
    fun iconColor(context:Context):Int
    fun akcColor(context: Context):Int
    fun setShadow (context: Context):Int
    fun setStatusBarColor(context: Context):Int
    fun setColorTextStatusBar():Boolean
    fun stylePopupTheme():Int
    fun backgroundResDialogFragment():Int
}