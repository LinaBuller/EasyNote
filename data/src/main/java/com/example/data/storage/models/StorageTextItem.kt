package com.example.data.storage.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.storage.ConstantsDbName
import java.util.UUID

@Entity(tableName = ConstantsDbName.ITEMS_TEXT_TABLE_NAME)

data class StorageTextItem(
    @PrimaryKey
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_ID)
    var itemTextId: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_FOREIGN_ID)
    var foreignId: Long = 0,
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_TITLE)
    var text: String = "",
    @ColumnInfo(name = ConstantsDbName.ITEMS_TEXT_POSITION)
    override var position: Int = 0
) : MultiItem(position)