package com.buller.mysqlite.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buller.mysqlite.data.ConstantsDbName

@Entity(tableName = ConstantsDbName.FAV_COLOR_TABLE_NAME)
class FavoriteColor (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.FAV_COLOR_ID)
    val id:Int=0,

    @ColumnInfo(name = ConstantsDbName.FAV_COLOR_NUMBER)
    val number:Int
    )