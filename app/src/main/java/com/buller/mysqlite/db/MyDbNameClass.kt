package com.buller.mysqlite.db

import android.provider.BaseColumns

object MyDbNameClass : BaseColumns {


    const val COLUMN_NAME_TIME = "time"
    const val TABLE_NAME = "my_table"
    const val COLUMN_NAME_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"
    const val COLUMN_NAME_IMAGE_URI = "image"
    const val TABLE_NAME_IMAGES = "images"
    const val KEY_ID = "id"
    const val COLOR_TITLE_FRAME = "color_title_frame"
    const val COLOR_CONTENT_FRAME = "color_content_frame"
    const val COLUMN_NAME_CATEGORY_TITLE = "category_title"
    const val TABLE_NAME_CATEGORY= "categories"
    const val TABLE_NAME_NOTE_AND_CATEGORY_CONNECTION = "connection_notes_and_categories"
    const val COLUMN_NAME_ID_NOTE = "id_note"
    const val COLUMN_NAME_ID_CATEGORY = "id_category"

    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "MyDb.db"

    const val CREATE_TABLE =
        "CREATE TABLE  IF NOT EXISTS " +
                "$TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME_TITLE TEXT, " +
                "$COLUMN_NAME_CONTENT TEXT, " +
                "$COLUMN_NAME_TIME TEXT, " +
                "$COLOR_TITLE_FRAME INTEGER, " +
                "$COLOR_CONTENT_FRAME INTEGER)"

    const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val CREATE_TABLE_IMAGES = "CREATE TABLE  IF NOT EXISTS " +
            "$TABLE_NAME_IMAGES (${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_IMAGE_URI TEXT,$KEY_ID INTEGER)"

    const val CREATE_TABLE_CATEGORIES = "CREATE TABLE  IF NOT EXISTS " +
            "$TABLE_NAME_CATEGORY (${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_CATEGORY_TITLE TEXT)"

    const val CREATE_TABLE_NOTES_CATEGORIES_CONNECTION = "CREATE TABLE  IF NOT EXISTS " +
            "$TABLE_NAME_NOTE_AND_CATEGORY_CONNECTION (${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_NAME_ID_NOTE INTEGER,$COLUMN_NAME_ID_CATEGORY INTEGER)"
}