package com.example.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.storage.models.NoteWithCategoriesCrossRef
import com.example.data.storage.models.StorageCategory
import com.example.data.storage.models.StorageFavoriteColor
import com.example.data.storage.models.StorageImage
import com.example.data.storage.models.StorageImageItem
import com.example.data.storage.models.StorageNote
import com.example.data.storage.models.StorageTextItem


@Database(
    entities = [StorageNote::class, StorageImage::class, StorageCategory::class, NoteWithCategoriesCrossRef::class,
        StorageFavoriteColor::class, StorageTextItem::class, StorageImageItem::class],
    version = ConstantsDbName.DATABASE_VERSION
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun getDao(): NotesDao

    companion object {
        fun newInstance(context: Context): NotesDatabase {
            return Room.databaseBuilder(
                context,
                NotesDatabase::class.java,
                ConstantsDbName.DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
}
