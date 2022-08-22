package com.buller.mysqlite.model

import android.os.Parcelable
import androidx.room.*
import com.buller.mysqlite.data.ConstantsDbName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = ConstantsDbName.IMAGES_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf(ConstantsDbName.NOTE_ID),
        childColumns = arrayOf(ConstantsDbName.IMAGES_FOREIGN_ID),
        onDelete = ForeignKey.CASCADE
    )]
)
@Parcelize
data class Image(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ConstantsDbName.IMAGES_ID)
    var id: Long = 0,

    @ColumnInfo(name = ConstantsDbName.IMAGES_FOREIGN_ID)
    var foreignId:  Long = 0,

    @ColumnInfo(name = ConstantsDbName.IMAGES_IMAGE_URI)
    var uri: String = "",

    @Ignore
    var isDelete: Boolean = false

) : Parcelable {

    constructor(foreignId:  Long, uri: String) : this(id = 0)
    constructor(uri: String) : this(id = 0, foreignId = 0)
    constructor() : this(0,0,"")

    fun isNew(): Boolean {
        return id == 0L
    }

}