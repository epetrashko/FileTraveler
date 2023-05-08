package com.epetrashko.data.di

import android.content.Context
import androidx.room.Room
import com.epetrashko.data.db.AppDatabase
import com.epetrashko.data.db.FilesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun database(@ApplicationContext applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, AppDatabase.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun filesDao(db: AppDatabase): FilesDao =
        db.filesDao()


}