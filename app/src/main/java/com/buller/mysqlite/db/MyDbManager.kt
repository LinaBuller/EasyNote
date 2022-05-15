package com.buller.mysqlite.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.buller.mysqlite.ItemCategory
import com.buller.mysqlite.ItemCategoryBase
import com.buller.mysqlite.ItemCategorySelect
import com.buller.mysqlite.NoteCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {
    private val myDbHelper = MyDbHelper(context)
    private var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    fun closeDb() {
        myDbHelper.close()
    }

    fun insertDb(
        title: String,
        content: String,
        time: String,
        colorTitle: Int,
        colorContent: Int
    ): Long {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
            put(MyDbNameClass.COLOR_TITLE_FRAME, colorTitle)
            put(MyDbNameClass.COLOR_CONTENT_FRAME, colorContent)
        }

        return db?.insert(MyDbNameClass.TABLE_NAME, null, values)!!
    }

    @SuppressLint("Range")
    suspend fun readDb(searchText: String): MutableList<NoteItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<NoteItem>()
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME,
            null,
            selection,
            arrayOf("%$searchText%"),
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))
            val dataColorFrameTitle: Int =
                cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLOR_TITLE_FRAME))
            val dataColorFrameContent: Int =
                cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLOR_CONTENT_FRAME))
            dataList.add(
                NoteItem(
                    dataTitle,
                    dataContent,
                    dataId,
                    dataTime,
                    dataColorFrameTitle,
                    dataColorFrameContent
                )
            )
        }

        cursor.close()
        return@withContext dataList
    }

    fun removeItemDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    fun updateItemDb(
        title: String,
        content: String,
        id: Int,
        time: String,
        colorTitle: Int,
        colorContent: Int
    ) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
            put(MyDbNameClass.COLOR_TITLE_FRAME, colorTitle)
            put(MyDbNameClass.COLOR_CONTENT_FRAME, colorContent)
        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }


    @SuppressLint("Range")
    suspend fun readDbImageForForeignId(foreignId: Int): MutableList<ImageItem> =
        withContext(Dispatchers.IO) {
            val dataList = mutableListOf<ImageItem>()
            val cursor = db?.query(
                MyDbNameClass.TABLE_NAME_IMAGES,
                null,
                "${MyDbNameClass.KEY_ID} = $foreignId",
                null,
                null,
                null,
                null
            )
            while (cursor?.moveToNext()!!) {
                val dataIdUri = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val dataUri =
                    cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
                dataList.add(ImageItem(dataUri, foreignId, dataIdUri))
            }

            cursor.close()
            return@withContext dataList
        }

    @SuppressLint("Range")
    suspend fun readAllDbImages(): MutableList<ImageItem> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<ImageItem>()
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME_IMAGES,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataIdUri = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataUri =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
            val dataUriForeignId = cursor.getInt(cursor.getColumnIndex(MyDbNameClass.KEY_ID))
            dataList.add(ImageItem(dataUri, dataUriForeignId, dataIdUri))
        }
        cursor.close()
        return@withContext dataList
    }


    fun updateImagesDb(uri: String, id: Int) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
        }
        db?.update(MyDbNameClass.TABLE_NAME_IMAGES, values, selection, null)
    }

    fun insertImagesDb(uri: String, foreignId: Int) {
        val values = ContentValues().apply {
            put(MyDbNameClass.KEY_ID, foreignId)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_IMAGES, null, values)
    }

    //Удалить все картинки из заметки
    fun removeItemImageDb(foreignId: Int) {
        val selection = BaseColumns._ID + "=$foreignId"
        db?.delete(MyDbNameClass.TABLE_NAME_IMAGES, selection, null)
    }

    //Удалить одну картинку из всех что есть в заметке
    fun removeImagesDbforForeignId(id: Int) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME_IMAGES, selection, null)
    }

    @SuppressLint("Range")
    fun readDbCategories(): MutableList<ItemCategoryBase> {
        val dataList = ArrayList<ItemCategoryBase>()
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME_CATEGORY,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataIdCategory = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitleCategory =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CATEGORY_TITLE))
            dataList.add(ItemCategory(dataIdCategory, dataTitleCategory))
        }
        cursor.close()
        return dataList
    }

    @SuppressLint("Range")
    fun readDbCategoriesSelect(): MutableList<ItemCategoryBase> {
        val dataList = ArrayList<ItemCategoryBase>()
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME_CATEGORY,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataIdCategory = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitleCategory =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CATEGORY_TITLE))
            dataList.add(ItemCategorySelect(dataIdCategory, dataTitleCategory))
        }
        cursor.close()
        return dataList
    }

    fun insertDbCategories(title: String): Long {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_CATEGORY_TITLE, title)
        }
        return db?.insert(MyDbNameClass.TABLE_NAME_CATEGORY, null, values)!!
    }

    fun updateDbCategories(id: Long, title: String) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_CATEGORY_TITLE, title)
        }
        db?.update(MyDbNameClass.TABLE_NAME_CATEGORY, values, selection, null)
    }

    fun removeDbCategories(id: Long) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME_CATEGORY, selection, null)
    }

    fun insertDBConnectionNotesAndCategory(idNotes: Long, id: Long) {
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_ID_NOTE, idNotes)
            put(MyDbNameClass.COLUMN_NAME_ID_CATEGORY, id)
        }
        db?.insert(MyDbNameClass.TABLE_NAME_NOTE_AND_CATEGORY_CONNECTION, null, values)
    }

    fun removeDBConnectionNotesAndCategory(id: Long, idNotes: Int) {
        TODO("Not yet implemented")
    }

    @SuppressLint("Range")
    fun readDbCategoriesSpecialNote(idNote: Int): MutableSet<Int> {
        val dataList = mutableSetOf<Int>()
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME_NOTE_AND_CATEGORY_CONNECTION,
            null,
            "${MyDbNameClass.COLUMN_NAME_ID_NOTE} = $idNote",
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataIdCategory =
                cursor.getLong(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_ID_CATEGORY))
            dataList.add(dataIdCategory.toInt())
        }
        cursor.close()
        return dataList
    }

    fun removeCategoriesForIdNote(firstTableId: Int) {
        val selection = MyDbNameClass.COLUMN_NAME_ID_NOTE + "=$firstTableId"
        db?.delete(MyDbNameClass.TABLE_NAME_NOTE_AND_CATEGORY_CONNECTION, selection, null)
    }

    @SuppressLint("Recycle", "Range")
    fun readJoinTableConnectionWithTableCategory(id_note: Int): ArrayList<NoteCategory> {
        //SELECT id_category,category_title FROM connection_notes_and_categories as nc JOIN categories as c ON nc.id_category = c._id  WHERE id_note = 1
        val arrayList = ArrayList<NoteCategory>()
        val cursor = db?.rawQuery(
            "SELECT id_category,category_title FROM connection_notes_and_categories AS NC JOIN categories AS C ON NC.id_category = C._id WHERE id_note = ?",
            arrayOf(id_note.toString())
        )

        while (cursor!!.moveToNext()) {
            val id_category_connection =
                cursor.getInt(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_ID_CATEGORY))
            val title_category_connection =
                cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CATEGORY_TITLE))
            arrayList.add(NoteCategory(id_category_connection,title_category_connection))
        }
        return arrayList
    }
}