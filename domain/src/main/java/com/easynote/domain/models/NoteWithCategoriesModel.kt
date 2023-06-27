package com.easynote.domain.models

data class NoteWithCategoriesModel(
    var note: com.easynote.domain.models.Note,
    var listOfCategories: List<com.easynote.domain.models.Category>? = null
)