package com.easynote.domain.models

data class Category(
    var idCategory: Long = 0,
    var titleCategory: String = "No title",
    override var position: Int = 0
) : ReorderableEntity {

}
