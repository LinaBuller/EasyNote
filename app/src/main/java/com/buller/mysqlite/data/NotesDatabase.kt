package com.buller.mysqlite.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.buller.mysqlite.model.*


@Database(
    entities = [Note::class,
        Image::class,
        Category::class,
        NoteWithCategoriesCrossRef::class,
        FavoriteColor::class], version = ConstantsDbName.DATABASE_VERSION, exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null
        fun getDatabase(context: Context): NotesDatabase? {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) {
//                return tempInstance
//            }
//            synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    NotesDatabase::class.java,
//                    ConstantsDbName.DATABASE_NAME
//                ).build()
//                INSTANCE = instance
//                return INSTANCE as NotesDatabase
//            }

            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE==null){
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            NotesDatabase::class.java,
                            ConstantsDbName.DATABASE_NAME
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }

}
