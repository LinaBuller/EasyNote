package com.buller.mysqlite.fragments.add.multiadapter

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buller.mysqlite.data.ConstantsDbName

@Entity(tableName = ConstantsDbName.ITEMS_TEXT_TABLE_NAME)

data class TextItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_ID)
    var itemTextId: Long = 0,
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_FOREIGN_ID)
    var foreignId: Long = 0,
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_TITLE)
    var text: String = "",
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_POSITION)
    override var position: Int = 0
) : MultiItem(position)