package com.buller.mysqlite.utils.theme

import android.content.Context
import com.dolatkia.animatedThemeManager.AppTheme

interface BaseTheme: AppTheme {
    fun backgroundColor(context:Context):Int
    fun backgroundDrawer(context:Context):Int
    fun backgroundBottomDrawer(context:Context):Int
    fun textColor(context:Context):Int
    fun iconColor(context:Context):Int
}