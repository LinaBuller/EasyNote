package com.buller.mysqlite

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class NoteCategory(override val id: Long, var title:String) : Parcelable,ItemCategoryBase(id)