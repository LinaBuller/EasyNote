package com.buller.mysqlite.theme

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.buller.mysqlite.R

class LightTheme: BaseTheme {
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
        return ContextCompat.getColor(context, R.color.element_light)
    }

    override fun setColorTextStatusBar(): Boolean {
        return true
    }

    override fun stylePopupTheme(): Int {
        return R.style.LightPopupTheme
    }

    override fun styleDialogTheme(): Int {
       return R.style.DialogLight
    }

    override fun styleDialogAddCategory(): Int {
        return R.style.CustomTextInputLayoutLight
    }

    override fun backgroundResDialogFragment(): Int {
        return R.drawable.custom_button_and_dialog_fragment_background_light
    }

    override fun warningColor(context: Context): Int {
        return ResourcesCompat.getColor(context.resources, R.color.red_delete_light,null)
    }

    override fun id(): Int = 0
}