package com.buller.mysqlite.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.buller.mysqlite.R

class DarkTheme : BaseTheme {
    override fun backgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.background_dark)
    }

    override fun backgroundColorEditText(context: Context): Int {
        return ContextCompat.getColor(context,R.color.background_dark)
    }

    override fun backgroundDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.element_dark)
    }

    override fun backgroundBottomDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.background_dark)
    }

    override fun textColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun textColorTabSelect(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun textColorTabUnselect(context: Context): Int {
        return ContextCompat.getColor(context, R.color.dark_gray)
    }

    override fun iconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun akcColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.akcient_dark)
    }

    override fun setShadow(context: Context): Int {
        return ContextCompat.getColor(context, R.color.akcient_dark)
    }

    override fun setStatusBarColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.element_dark)
    }

    override fun stylePopupTheme(): Int {
       return R.style.DarkPopupTheme
    }

    override fun styleDialogTheme(): Int {
        return R.style.DialogDark
    }

    override fun styleDialogAddCategory(): Int {
        return R.style.CustomTextInputLayoutDark
    }

    override fun backgroundResDialogFragment(): Int {
       return R.drawable.custom_button_and_dialog_fragment_background_dark
    }

    override fun setColorTextStatusBar(): Boolean {
        return false
    }
    override fun id(): Int = 1


}