package com.easynote.domain.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CurrentTimeInFormat {

    fun getCurrentTime(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy KK:mm")
        return LocalDateTime.now().format(formatter)
    }
}