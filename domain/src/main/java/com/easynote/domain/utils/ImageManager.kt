package com.easynote.domain.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.easynote.domain.models.Image
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.UUID


class ImageManager(val context: Context) : KoinComponent {
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
                val newBitmap = Picasso.get().load(uris[i].toString())
                    .resize(tempList[i][WIDTH_IMAGE], tempList[i][HEIGHT_IMAGE]).get()
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

    fun uriToBitmap(image: Image): Bitmap? {

        return try {
            val imgFile = File(image.uri)
            if (imgFile.exists()) {

                BitmapFactory.decodeFile(imgFile.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    fun loadTempFile(inputStream: InputStream, tempFile: File): File? {
        return try {
            val dso = tempFile.outputStream()
            inputStream.use { input ->
                dso.use { output ->
                    input.copyTo(output)
                }
            }
            inputStream.close()
            dso.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    fun createImageTempFile(id: String): File? {
        val mContext = WeakReference(context)
        mContext.get()?.let {
            val rootPath = File(it.filesDir, "Multimedia")
            if (!rootPath.exists()) rootPath.mkdirs()
            val bkpFile = File(rootPath, "img_${id}.jpg")
            bkpFile.createNewFile()
            return bkpFile
        }
        return null
    }

    fun createFile(bitmap: Bitmap, id: UUID): String {
        val mContext = WeakReference(context)
        var path = ""
        try {
            mContext.get()?.let {

                var file = File(it.filesDir, "Multimedia")
                if (!file.exists()) file.mkdir()
                file = File(file, "img_${id}.jpg")

                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                out.flush()
                out.close()
                path = file.absolutePath
            }
            Log.i("Segregation", "Image saved.")
        } catch (e: Exception) {
            Log.i("Segregation", "Failed to save image.")
        }
        return path
    }
}