package com.example.data.storage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.data.storage.ConstantsDbName
import com.example.data.storage.room.models.StorageNoteWithCategoriesCrossRef
import com.example.data.storage.room.models.StorageCategory
import com.example.data.storage.room.models.StorageFavoriteColor
import com.example.data.storage.room.models.StorageImage
import com.example.data.storage.room.models.StorageImageItem
import com.example.data.storage.room.models.StorageNote
import com.example.data.storage.room.models.StorageTextItem

@Database(
    entities = [StorageNote::class, StorageImage::class, StorageCategory::class, StorageNoteWithCategoriesCrossRef::class,
        StorageFavoriteColor::class, StorageTextItem::class, StorageImageItem::class],
    version = ConstantsDbName.DATABASE_VERSION
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun getDao(): NotesDao

    companion object {
        fun newInstance(context: Context): LocalDatabase {
            return Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                ConstantsDbName.DATABASE_NAME
            ).fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE)
                .build()


//            return Room.databaseBuilder(
//                context,
//                LocalDatabase::class.java,
//                ConstantsDbName.DATABASE_NAME
//            ).fallbackToDestructiveMigration().setJournalMode(JournalMode.TRUNCATE)
//                .build()
        }
    }
}
