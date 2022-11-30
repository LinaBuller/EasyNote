package com.buller.mysqlite.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.buller.mysqlite.data.ConstantsDbName
import kotlinx.parcelize.Parcelize

@Entity(tableName = ConstantsDbName.NOTE_TABLE_NAME)

@Parcelize
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.NOTE_ID)
    var id: Long = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_TITLE)
    var title: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_CONTENT)
    var content: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_TIME)
    var time: String = "",

    @ColumnInfo(name = ConstantsDbName.NOTE_FRAME_COLOR_TITLE)
    var colorFrameTitle: Int = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_FRAME_COLOR_CONTENT)
    var colorFrameContent: Int = 0,

    @ColumnInfo(name = ConstantsDbName.NOTE_IS_DELETED)
    var isDeleted:Boolean = false

) : Parcelable