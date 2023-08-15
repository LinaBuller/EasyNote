package com.easynote.domain.models

data class Note(
    var id: Long = 0,
    var title: String = "",
    var content: String = "",
    var text: String = "",
    var time: String = "",
    var gradientColorFirst: Int = 0,
    var gradientColorFirstH:Float = 0F,
    var gradientColorFirstS:Float = 0F,
    var gradientColorFirstL:Float = 0F,
    var gradientColorSecond: Int = 0,
    var gradientColorSecondH:Float = 0F,
    var gradientColorSecondS:Float = 0F,
    var gradientColorSecondL:Float = 0F,
    var isDeleted: Boolean = false,
    var isPin: Boolean = false,
    var isFavorite: Boolean = false,
    var isArchive: Boolean = false
)