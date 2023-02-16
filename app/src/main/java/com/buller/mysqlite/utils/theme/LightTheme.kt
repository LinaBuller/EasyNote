package com.buller.mysqlite.utils.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.buller.mysqlite.R

class LightTheme:BaseTheme {
    override fun backgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.background_light)
    }

    override fun backgroundDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.element_light)
    }

    override fun backgroundColorEditText(context: Context): Int {
        return ContextCompat.getColor(context,R.color.background_light)
    }

    override fun backgroundBottomDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.element_light)
    }

    override fun textColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.dark_gray)
    }

    override fun textColorTabSelect(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun textColorTabUnselect(context: Context): Int {
        return ContextCompat.getColor(context, R.color.grey)
    }

    override fun iconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.akcient_light)
    }

    override fun akcColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.akcient_light)
    }

    override fun setShadow(context: Context): Int {
        return ContextCompat.getColor(context, R.color.akcient_light)
    }

    override fun setStatusBarColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.dark_gray)
    }

    override fun stylePopupTheme(): Int {
        return R.style.LightPopupTheme
    }

    override fun id(): Int = 0
}