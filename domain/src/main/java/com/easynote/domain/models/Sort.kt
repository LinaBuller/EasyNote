package com.easynote.domain.models

class Sort(
    val sortColumn: String = "n.is_pin",
    val sortOrder: Int = 1,
    val date: Array<Int>? = null
) {
}