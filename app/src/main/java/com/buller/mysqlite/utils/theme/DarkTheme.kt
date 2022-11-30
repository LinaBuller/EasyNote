package com.buller.mysqlite.utils.theme

import android.content.Context
import androidx.core.content.ContextCompat
import com.buller.mysqlite.R

class DarkTheme : BaseTheme {
    override fun backgroundColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_4)
    }

    override fun backgroundDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun backgroundBottomDrawer(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_3)
    }


    override fun textColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun iconColor(context: Context): Int {
        return ContextCompat.getColor(context, R.color.color_2)
    }

    override fun id(): Int = 1
}