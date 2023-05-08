package com.epetrashko.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.epetrashko.data.model.FileHashModel

@Database(entities = [FileHashModel::class], version = AppDatabase.VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filesDao(): FilesDao

    companion object {
        const val DATABASE_NAME = "files_database"
        const val VERSION = 1
    }
}