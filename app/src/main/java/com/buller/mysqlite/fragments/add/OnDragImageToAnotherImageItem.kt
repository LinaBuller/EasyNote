package com.buller.mysqlite.fragments.add

import com.easynote.domain.models.Image
import com.easynote.domain.models.ImageItem

interface OnDragImageToAnotherImageItem {
    fun setImageFromTarget(image: Image,targetPosition:Int, targetImageItem: ImageItem)
    fun removeSourceImage(image: Image,sourceImageItem: ImageItem)
}