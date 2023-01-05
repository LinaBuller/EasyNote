package com.buller.mysqlite.utils.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.buller.mysqlite.R

class LightTheme:BaseTheme {
    override fun backgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.white)
    }

    override fun backgroundDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun backgroundBottomDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_3)
    }

    override fun textColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.dark_gray)
    }

    override fun iconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.dark_gray)
    }

    override fun id(): Int = 0
}