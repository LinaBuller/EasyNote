package com.example.data.storage.models

import androidx.annotation.NonNull
import androidx.room.*
import com.example.data.storage.ConstantsDbName

@Entity(tableName = ConstantsDbName.CATEGORY_TABLE_NAME)
data class StorageCategory(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.CATEGORY_ID)
    var idCategory: Long = 0,
    @ColumnInfo(name = ConstantsDbName.CATEGORY_TITLE)
    var titleCategory: String = "No title"
)
