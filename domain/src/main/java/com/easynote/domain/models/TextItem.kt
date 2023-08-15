package com.easynote.domain.models

import java.util.UUID


data class TextItem(
    var itemTextId: String = UUID.randomUUID().toString(),
    var foreignId: Long = 0,
    var text: String = "",
    override var position: Int = 0
) : MultiItem(position) {

    fun isEmpty(): Boolean {
        return text.isBlank()
    }

}