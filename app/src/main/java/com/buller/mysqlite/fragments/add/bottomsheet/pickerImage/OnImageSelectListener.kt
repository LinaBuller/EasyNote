package com.buller.mysqlite.fragments.add.bottomsheet.pickerImage

import android.net.Uri

interface OnImageSelectListener {
    fun onImagesSelected(uris: List<Uri>)
}