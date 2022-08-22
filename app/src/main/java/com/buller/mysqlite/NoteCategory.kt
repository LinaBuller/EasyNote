package com.buller.mysqlite

import android.os.Parcelable
import com.buller.mysqlite.ItemCategoryBase
import kotlinx.parcelize.Parcelize

@Parcelize
class NoteCategory(

    override val id: Long,

    override var title:String
    ) : Parcelable, ItemCategoryBase(id,title)