package com.buller.mysqlite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class NoteCategory(val id:Int, val title:String) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}