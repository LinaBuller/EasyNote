package com.buller.mysqlite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.buller.mysqlite.data.ConstantsDbName

//@Entity(tableName = ConstantsDbName.N_A_C_CON_TABLE_NAME)
class ConnectionNoteAndCategories(
    //@ColumnInfo(name = ConstantsDbName.N_A_C_CON_ID_NOTE)
    val noteId: Int = 0,
    //@ColumnInfo(name = ConstantsDbName.N_A_C_CON_ID_CATEGORY)
    val categoryId: Int = 0
){
    //@PrimaryKey (autoGenerate = true)
    var connectionId: Int =0

    fun setCon(id:Int){
        connectionId = id
    }
}