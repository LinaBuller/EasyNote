package com.buller.mysqlite.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class NoteItem(
    var title: String = "empty",
    var content: String = "empty",
    var id: Int = 0,
    var time: String = "empty",
    var colorFrameTitle: Int = 0,
    var colorFrameContent: Int = 0
):Parcelable