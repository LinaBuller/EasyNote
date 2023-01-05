package com.buller.mysqlite.model

import androidx.room.*
import com.buller.mysqlite.data.ConstantsDbName

@Entity(
    tableName = ConstantsDbName.FAV_COLOR_TABLE_NAME, indices = [Index(
        value = arrayOf(
            ConstantsDbName.FAV_COLOR_NUMBER
        ), unique = true
    )]
)
class FavoriteColor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.FAV_COLOR_ID)
    val id: Int = 0,

    @ColumnInfo(name = ConstantsDbName.FAV_COLOR_NUMBER)
    val number: Int
)