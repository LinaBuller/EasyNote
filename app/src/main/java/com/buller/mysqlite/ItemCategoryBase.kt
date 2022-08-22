package com.buller.mysqlite

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buller.mysqlite.data.ConstantsDbName
import kotlinx.parcelize.Parcelize
//@Entity(tableName = ConstantsDbName.CATEGORY_TABLE_NAME)
@Parcelize
open class ItemCategoryBase(
    //@PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = ConstantsDbName.CATEGORY_ID)
    open val id: Long = 0,
    //@ColumnInfo(name = ConstantsDbName.CATEGORY_TITLE)
    open val title: String = ""
) : Parcelable {
    constructor(title: String) : this(id=0)

}