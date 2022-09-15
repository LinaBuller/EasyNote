package com.buller.mysqlite.model

import androidx.annotation.NonNull
import androidx.room.*
import com.buller.mysqlite.data.ConstantsDbName

@Entity(tableName = ConstantsDbName.CATEGORY_TABLE_NAME)
data class Category(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.CATEGORY_ID)
    var idCategory: Long = 0,
    @ColumnInfo(name = ConstantsDbName.CATEGORY_TITLE)
    val titleCategory: String = "No title"
)
