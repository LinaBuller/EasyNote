package com.easynote.domain.models


data class TextItem(
    var itemTextId: Long = 0,
    var foreignId: Long = 0,
    var text: String = "",
    override var position: Int = 0
) : com.easynote.domain.models.MultiItem(position)