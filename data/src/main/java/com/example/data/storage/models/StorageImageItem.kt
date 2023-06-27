package com.example.data.storage.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.data.storage.ConstantsDbName

@Entity(tableName = ConstantsDbName.ITEMS_IMAGE_TABLE_NAME)
data class StorageImageItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_ID)
    var imageItemId: Long = 0L,

    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_FOREIGN_ID)
    var foreignId: Long = 0L,

    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_POSITION)
    override var position: Int = 0,

    @Ignore
    var listImageItems: ArrayList<StorageImage> = arrayListOf()

) : MultiItem(position) {

}