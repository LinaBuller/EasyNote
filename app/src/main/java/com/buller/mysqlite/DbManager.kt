package com.buller.mysqlite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.buller.mysqlite.data.ConstantsDbName
import com.buller.mysqlite.model.Category
import com.buller.mysqlite.model.Image
import com.buller.mysqlite.model.Note
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbManager{
    var db: SQLiteDatabase? = null
    val auth = Firebase.auth

    fun insertDb(
        title: String,
        content: String,
        time: String,
        colorTitle: Int,
        colorContent: Int
    ): Long {
        val values = ContentValues().apply {
            put(ConstantsDbName.NOTE_TITLE, title)
            put(ConstantsDbName.NOTE_CONTENT, content)
            put(ConstantsDbName.NOTE_TIME, time)
            put(ConstantsDbName.NOTE_FRAME_COLOR_TITLE, colorTitle)
            put(ConstantsDbName.NOTE_FRAME_COLOR_CONTENT, colorContent)
        }

        return db?.insert(ConstantsDbName.NOTE_TABLE_NAME, null, values)!!
    }

    @SuppressLint("Range")
    suspend fun readDb(searchText: String):  ArrayList<Note> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Note>()
        val selection = "${ConstantsDbName.NOTE_TITLE} like ?"
        val cursor = db?.query(
            ConstantsDbName.NOTE_TABLE_NAME,
            null,
            selection,
            arrayOf("%$searchText%"),
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_CONTENT))
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TIME))
            val dataColorFrameTitle: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_TITLE))
            val dataColorFrameContent: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_CONTENT))
//            dataList.add(
//                Note(
//                    dataId,
//                    dataTitle,
//                    dataContent,
//                    dataTime,
//                    dataColorFrameTitle,
//                    dataColorFrameContent
//                )
//            )
        }

        cursor.close()
        return@withContext dataList
    }

    fun removeItemDb(id: String) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(ConstantsDbName.NOTE_TABLE_NAME, selection, null)
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
            put(ConstantsDbName.NOTE_TITLE, title)
            put(ConstantsDbName.NOTE_CONTENT, content)
            put(ConstantsDbName.NOTE_TIME, time)
            put(ConstantsDbName.NOTE_FRAME_COLOR_TITLE, colorTitle)
            put(ConstantsDbName.NOTE_FRAME_COLOR_CONTENT, colorContent)
        }
        db?.update(ConstantsDbName.NOTE_TABLE_NAME, values, selection, null)
    }


    @SuppressLint("Range")
    suspend fun readDbImageForForeignId(foreignId: Int): MutableList<Image> =
        withContext(Dispatchers.IO) {
            val dataList = mutableListOf<Image>()
            val cursor = db?.query(
                ConstantsDbName.IMAGES_TABLE_NAME,
                null,
                "${ConstantsDbName.NOTE_ID} = $foreignId",
                null,
                null,
                null,
                null
            )
            while (cursor?.moveToNext()!!) {
                val dataIdUri = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
                val dataUri =
                    cursor.getString(cursor.getColumnIndex(ConstantsDbName.IMAGES_IMAGE_URI))
                //dataList.add(Image(dataIdUri, foreignId,dataUri))
            }

            cursor.close()
            return@withContext dataList
        }

    @SuppressLint("Range")
    suspend fun readAllDbImages(): MutableList<Image> = withContext(Dispatchers.IO) {
        val dataList = ArrayList<Image>()
        val cursor = db?.query(
            ConstantsDbName.IMAGES_TABLE_NAME,
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
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.IMAGES_IMAGE_URI))
            val dataUriForeignId = cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_ID))
            //dataList.add(Image(dataIdUri, dataUriForeignId, dataUri))
        }
        cursor.close()
        return@withContext dataList
    }


    fun updateImagesDb(uri: String, id: Int) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(ConstantsDbName.IMAGES_IMAGE_URI, uri)
        }
        db?.update(ConstantsDbName.IMAGES_TABLE_NAME, values, selection, null)
    }

    fun insertImagesDb(uri: String, foreignId: Int) {
        val values = ContentValues().apply {
            put(ConstantsDbName.NOTE_ID, foreignId)
            put(ConstantsDbName.IMAGES_IMAGE_URI, uri)
        }
        db?.insert(ConstantsDbName.IMAGES_TABLE_NAME, null, values)
    }

    //Удалить все картинки из заметки
    fun removeItemImageDb(foreignId: Int) {
        val selection = BaseColumns._ID + "=$foreignId"
        db?.delete(ConstantsDbName.IMAGES_TABLE_NAME, selection, null)
    }

    //Удалить одну картинку из всех что есть в заметке
    fun removeImagesDbforForeignId(id: Int) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(ConstantsDbName.IMAGES_TABLE_NAME, selection, null)
    }

    @SuppressLint("Range")
    fun readDbCategories(): ArrayList<Category> {
        val dataList = ArrayList<Category>()
        val cursor = db?.query(
            ConstantsDbName.CATEGORY_TABLE_NAME,
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
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.CATEGORY_TITLE))
            dataList.add(Category(dataIdCategory, dataTitleCategory))
        }
        cursor.close()
        return dataList
    }

    @SuppressLint("Range")
    fun readDbCategoriesSelect(): ArrayList<ItemCategorySelect> {
        val dataList = ArrayList<ItemCategorySelect>()
        val cursor = db?.query(
            ConstantsDbName.CATEGORY_TABLE_NAME,
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
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.CATEGORY_TITLE))
            dataList.add(ItemCategorySelect(dataIdCategory, dataTitleCategory))
        }
        cursor.close()
        return dataList
    }

    fun insertDbCategories(title: String): Long {
        val values = ContentValues().apply {
            put(ConstantsDbName.CATEGORY_TITLE, title)
        }
        return db?.insert(ConstantsDbName.CATEGORY_TABLE_NAME, null, values)!!
    }

    fun updateDbCategories(id: Long, title: String) {
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(ConstantsDbName.CATEGORY_TITLE, title)
        }
        db?.update(ConstantsDbName.CATEGORY_TABLE_NAME, values, selection, null)
    }

    fun removeDbCategories(id: Long) {
        val selection = BaseColumns._ID + "=$id"
        db?.delete(ConstantsDbName.CATEGORY_TABLE_NAME, selection, null)
    }

    fun insertDBConnectionNotesAndCategory(idNotes: Long, id: Long) {
        val values = ContentValues().apply {
            put(ConstantsDbName.N_A_C_CON_ID_NOTE, idNotes)
            put(ConstantsDbName.N_A_C_CON_ID_CATEGORY, id)
        }
        db?.insert(ConstantsDbName.N_A_C_CON_TABLE_NAME, null, values)
    }

    fun removeCategoriesForIdNote(firstTableId: Int) {
        val selection = ConstantsDbName.N_A_C_CON_ID_NOTE + "=$firstTableId"
        db?.delete(ConstantsDbName.N_A_C_CON_TABLE_NAME, selection, null)
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
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.N_A_C_CON_ID_CATEGORY))
            val title_category_connection =
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.CATEGORY_TITLE))
            arrayList.add(NoteCategory(id_category_connection.toLong(), title_category_connection))
        }
        return arrayList
    }

    @SuppressLint("Range")
    fun readDbFromCategories(searchIDCategories: Int): ArrayList<Int> {
        val dataList = ArrayList<Int>()
        val selection = ConstantsDbName.N_A_C_CON_ID_CATEGORY + "=$searchIDCategories"
        val cursor = db?.query(
            ConstantsDbName.N_A_C_CON_TABLE_NAME,
            null,
            selection,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataIdNote = cursor.getInt(cursor.getColumnIndex(ConstantsDbName.N_A_C_CON_ID_NOTE))
            dataList.add(dataIdNote)
        }

        cursor.close()
        return dataList
    }

    @SuppressLint("Range")
    fun readDbSelectCategoryFromNote(id: Int): Note {
        var item = Note()
        val selection = BaseColumns._ID + "=$id"
        val cursor = db?.query(
            ConstantsDbName.NOTE_TABLE_NAME,
            null,
            selection,
            null,
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_CONTENT))
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TIME))
            val dataColorFrameTitle: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_TITLE))
            val dataColorFrameContent: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_CONTENT))
//            item = Note(
//                dataId,
//                dataTitle,
//                dataContent,
//                dataTime,
//                dataColorFrameTitle,
//                dataColorFrameContent
//            )
        }
        cursor.close()
        return item
    }

    @SuppressLint("Range")
    fun readDbFromSelectData(stringSelectData: String): ArrayList<Note> {
        val dataList = ArrayList<Note>()
        val selection = "${ConstantsDbName.NOTE_TIME} like ?"
        val cursor = db?.query(
            ConstantsDbName.NOTE_TABLE_NAME,
            null,
            selection,
            arrayOf("%$stringSelectData%"),
            null,
            null,
            null
        )
        while (cursor?.moveToNext()!!) {
            val dataTitle = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TITLE))
            val dataContent =
                cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_CONTENT))
            val dataId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTime = cursor.getString(cursor.getColumnIndex(ConstantsDbName.NOTE_TIME))
            val dataColorFrameTitle: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_TITLE))
            val dataColorFrameContent: Int =
                cursor.getInt(cursor.getColumnIndex(ConstantsDbName.NOTE_FRAME_COLOR_CONTENT))
            //dataList.add(
//                Note(
//                    dataId,
//                    dataTitle,
//                    dataContent,
//                    dataTime,
//                    dataColorFrameTitle,
//                    dataColorFrameContent
//                )
           // )
        }

        cursor.close()

        return dataList
    }
    interface ReadDataCallback {
        fun readData(list: ArrayList<Note>)
    }
    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        const val AD_NODE = "ad"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "categories"
        const val FAVS_NODE = "favs"
    }
}