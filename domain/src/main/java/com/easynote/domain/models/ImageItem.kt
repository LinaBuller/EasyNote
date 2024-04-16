package com.easynote.domain.models

import java.util.UUID

data class ImageItem(

    var imageItemId: Long = 0L,
    var uuid:String = UUID.randomUUID().toString(),
    var foreignId: Long = 0L,
    override var position: Int = 0,
    var listImageItems: List<Image> = arrayListOf(),
    override var isDeleted:Boolean = false

) : MultiItem(position) {
}