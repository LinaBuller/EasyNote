package com.easynote.domain.utils

import java.text.SimpleDateFormat
import java.util.*

object CurrentTimeInFormat {
    val time: Date = Calendar.getInstance().time
    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("dd-MM-yy KK:mm", Locale.getDefault())
        return formatter.format(time)
    }
}