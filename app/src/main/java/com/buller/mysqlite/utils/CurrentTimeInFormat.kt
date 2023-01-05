package com.buller.mysqlite.utils

import java.text.SimpleDateFormat
import java.util.*

object CurrentTimeInFormat {

    fun getCurrentTime(): String {
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy KK:mm", Locale.getDefault())
        return formatter.format(time)
    }
}