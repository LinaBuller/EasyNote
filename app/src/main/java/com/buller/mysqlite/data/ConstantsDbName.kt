package com.buller.mysqlite.data

import android.provider.BaseColumns

object ConstantsDbName : BaseColumns {
    const val DATABASE_NAME = "notes_database"
    const val DATABASE_VERSION = 1

    const val NOTE_TABLE_NAME = "notes"
    const val NOTE_ID = "note_id"
    const val NOTE_TITLE = "note_title"
    const val NOTE_CONTENT = "note_content"
    const val NOTE_TEXT = "note_text"
    const val NOTE_TIME = "note_time"
    const val NOTE_FRAME_COLOR_TITLE = "note_color_frame_title"
    const val NOTE_FRAME_COLOR_CONTENT = "note_color_frame_content"
    const val NOTE_IS_DELETED = "is_deleted"
    const val NOTE_IS_PIN = "is_pin"
    const val NOTE_IS_FAVORITE = "is_favorite"
    const val NOTE_IS_ARCHIVE = "is_archive"

    const val IMAGES_TABLE_NAME = "images"
    const val IMAGES_ID = "image_id"
    const val IMAGES_FOREIGN_ID = "foreign_id"
    const val IMAGES_IMAGE_URI = "image_uri"

    const val CATEGORY_TABLE_NAME= "categories"
    const val CATEGORY_ID = "category_id"
    const val CATEGORY_TITLE = "category_title"

    const val N_A_C_CON_TABLE_NAME = "connection_notes_and_categories"
    const val N_A_C_CON_ID_NOTE = "connection_note_id"
    const val N_A_C_CON_ID_CATEGORY = "connection_category_id"

    const val FAV_COLOR_TABLE_NAME="favorites_colors"
    const val FAV_COLOR_ID="color_id"
    const val FAV_COLOR_NUMBER="color_number"

    const val ITEMS_IMAGE_TABLE_NAME="image_items"
    const val ITEMS_IMAGE_ID="image_item_id"
    const val ITEMS_IMAGE_FOREIGN_ID ="image_item_foreign_id"
    const val ITEMS_IMAGE_POSITION="image_item_position"

    const val ITEMS_TEXT_TABLE_NAME="text_items"
    const val ITEMS_TEXT_ID="text_item_id"
    const val ITEMS_TEXT_FOREIGN_ID="text_item_foreign_id"
    const val ITEMS_TEXT_TITLE="text_item_title"
    const val ITEMS_TEXT_POSITION="text_item_position"

}