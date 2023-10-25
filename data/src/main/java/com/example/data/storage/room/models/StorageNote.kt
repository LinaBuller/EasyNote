package com.example.data.storage.room.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.storage.ConstantsDbName
import kotlinx.parcelize.Parcelize

@Entity(tableName = ConstantsDbName.NOTE_TABLE_NAME)

@Parcelize
data class StorageNote(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.NOTE_ID)
    var id: Long = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_TITLE)

    var title: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_CONTENT)
    var content: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_TEXT)
    var text: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_TIME)
    var time: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_FIRST)
    var gradientColorFirst: Int = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_FIRST_H)
    val gradientColorFirstH:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_FIRST_S)
    val gradientColorFirstS:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_FIRST_L)
    val gradientColorFirstL:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_SECOND)
    var gradientColorSecond: Int = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_SECOND_H)

    val gradientColorSecondH:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_SECOND_S)
    val gradientColorSecondS:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_GRADIENT_COLOR_SECOND_L)
    val gradientColorSecondL:Float = 0F,

    @ColumnInfo(name = ConstantsDbName.NOTE_IS_DELETED)
    var isDeleted: Boolean = false,

    @ColumnInfo(name = ConstantsDbName.NOTE_IS_PIN)
    var isPin: Boolean = false,

    @ColumnInfo(name = ConstantsDbName.NOTE_IS_FAVORITE)
    var isFavorite: Boolean = false,

    @ColumnInfo(name = ConstantsDbName.NOTE_IS_ARCHIVE)
    var isArchive: Boolean = false
): Parcelable