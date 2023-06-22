package com.buller.mysqlite.fragments.add.multiadapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.model.Image

@Entity(tableName = ConstantsDbName.ITEMS_IMAGE_TABLE_NAME)
data class ImageItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_ID)
    var imageItemId: Long = 0L,

    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_FOREIGN_ID)
    var foreignId: Long = 0L,

    @ColumnInfo(name = ConstantsDbName.ITEMS_IMAGE_POSITION)
    override var position: Int = 0,

    @Ignore
    var listImageItems: ArrayList<Image> = arrayListOf()

) : MultiItem(position) {

}