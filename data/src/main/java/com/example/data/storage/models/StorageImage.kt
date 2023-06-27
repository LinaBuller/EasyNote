package com.example.data.storage.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.data.storage.ConstantsDbName
import kotlinx.parcelize.Parcelize

@Entity(tableName = ConstantsDbName.IMAGES_TABLE_NAME)
@Parcelize

class StorageImage (
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

    constructor(foreignId: Long, uri: String) : this(id = 0)
    constructor(uri: String) : this(id = 0, foreignId = 0)
    constructor() : this(0, 0, "")
}
