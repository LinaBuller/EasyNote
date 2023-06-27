package com.easynote.domain.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageManager {
    val MAX_IMAGE_SIZE = 1000
    val WIDTH_IMAGE = 0
    val HEIGHT_IMAGE = 0

    fun getImageSize(uri: Uri, activity: Activity): List<Int> {
        val inStream = activity.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inStream, null, options)

        return listOf(options.outWidth, options.outHeight)
    }

    suspend fun imageResize(uris: ArrayList<Uri>, activity: Activity): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val tempList = ArrayList<List<Int>>()
            val bitmapList = ArrayList<Bitmap>()
            for (n in uris.indices) {
                val size = getImageSize(uris[n], activity)
                val imageRatio = size[WIDTH_IMAGE].toFloat() / size[HEIGHT_IMAGE].toFloat()
                if (imageRatio > 1) {
                    if (size[WIDTH_IMAGE] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                    } else {
                        tempList.add(listOf(size[WIDTH_IMAGE], size[HEIGHT_IMAGE]))
                    }
                } else {
                    if (size[HEIGHT_IMAGE] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                    } else {
                        tempList.add(listOf(size[WIDTH_IMAGE], size[HEIGHT_IMAGE]))
                    }
                }

            }

            for (i in uris.indices) {
                val newBitmap = Picasso
                    .get()
                    .load(uris[i].toString())
                    .resize(tempList[i][WIDTH_IMAGE], tempList[i][HEIGHT_IMAGE])
                    .get()
                bitmapList.add(newBitmap)

            }
            return@withContext bitmapList
        }


    fun chooseScaleType(im: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) {
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            im.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }
}