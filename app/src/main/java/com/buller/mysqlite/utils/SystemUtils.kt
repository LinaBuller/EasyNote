package com.buller.mysqlite.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

object SystemUtils {

     fun widthScreen(activity: Activity): Int {
        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            size.x

        } else {
            val curMetrics = wm.currentWindowMetrics
            val size = curMetrics.bounds
            size.width()
        }
    }

    fun showSoftKeyboard(view: View, context:Context){
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    fun hideSoftKeyboard(view: View,context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}