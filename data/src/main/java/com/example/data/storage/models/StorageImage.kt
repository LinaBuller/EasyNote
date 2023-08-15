package com.example.data.storage.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.data.storage.ConstantsDbName
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(tableName = ConstantsDbName.IMAGES_TABLE_NAME)
@Parcelize

class StorageImage (
    @PrimaryKey
    @ColumnInfo(name = ConstantsDbName.IMAGES_ID)
    var id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = ConstantsDbName.IMAGES_FOREIGN_ID)
    var foreignId:  Long = 0L,

    @ColumnInfo(name = ConstantsDbName.IMAGES_IMAGE_URI)
    var uri: String = "",

    @ColumnInfo(name = ConstantsDbName.IMAGES_IS_NEW)
    var isNew: Boolean = true,

    @ColumnInfo(name = ConstantsDbName.IMAGES_POSITION)
    var position:Int = 0,

    @Ignore
    var isDelete: Boolean = false

    ) : Parcelable {
}
