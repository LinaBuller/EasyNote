package com.buller.mysqlite.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDbHelper(context: Context):SQLiteOpenHelper(context,MyDbNameClass.DATABASE_NAME,
    null, MyDbNameClass.DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyDbNameClass.CREATE_TABLE)
        db?.execSQL(MyDbNameClass.CREATE_TABLE_IMAGES)
        db?.execSQL(MyDbNameClass.CREATE_TABLE_CATEGORIES)
        db?.execSQL(MyDbNameClass.CREATE_TABLE_NOTES_CATEGORIES_CONNECTION)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(MyDbNameClass.DROP_TABLE)
        onCreate(db)
    }
}