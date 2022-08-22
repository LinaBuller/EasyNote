package com.buller.mysqlite.utils

import android.app.Activity
import android.net.Uri
import com.buller.mysqlite.model.Image
import io.ak1.pix.helpers.PixBus
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options


object ImagePicker {
    private const val MAX_COUNT_IMAGES = 10

    fun setOptions(imageCount: Int = MAX_COUNT_IMAGES): Options {
        val options = Options().apply {
            count = imageCount
            isFrontFacing = false
            mode = Mode.Picture
            spanCount = 4
            path = "pix/images"
            preSelectedUrls = ArrayList<Uri>()
        }
        return options
    }
}