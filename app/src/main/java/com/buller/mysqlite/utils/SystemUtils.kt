package com.buller.mysqlite.utils

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager

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

}