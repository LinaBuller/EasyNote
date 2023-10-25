package com.easynote.domain.models

data class NoteWithCategories(
    var note:Note,
    var listOfCategories: List<Category>? = null
)