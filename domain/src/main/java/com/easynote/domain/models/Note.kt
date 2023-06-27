package com.easynote.domain.models

data class Note(
    var id: Long = 0,
    var title: String = "",
    var content: String = "",
    var text: String = "",
    var time: String = "",
    var colorFrameTitle: Int = 0,
    var colorFrameContent: Int = 0,
    var isDeleted: Boolean = false,
    var isPin: Boolean = false,
    var isFavorite: Boolean = false,
    var isArchive: Boolean = false
)