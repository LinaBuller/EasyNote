package com.buller.mysqlite.model

import android.os.Parcelable
import androidx.room.*
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.fragments.add.multiadapter.ImageItem
import kotlinx.parcelize.Parcelize

@Entity(tableName = ConstantsDbName.IMAGES_TABLE_NAME)
@Parcelize
data class Image(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.IMAGES_ID)
    var id: Long = 0L,

    @ColumnInfo(name = ConstantsDbName.IMAGES_FOREIGN_ID)
    var foreignId:  Long = 0L,

    @ColumnInfo(name = ConstantsDbName.IMAGES_IMAGE_URI)
    var uri: String = "",

    @Ignore
    var isDelete: Boolean = false

) : Parcelable {

    constructor(foreignId:  Long, uri: String) : this(id = 0)
    constructor(uri: String) : this(id = 0, foreignId = 0)
    constructor() : this(0,0,"")
}