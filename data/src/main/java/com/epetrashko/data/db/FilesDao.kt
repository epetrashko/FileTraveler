package com.epetrashko.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.epetrashko.data.model.FileHashModel

@Dao
interface FilesDao {

    @Query("SELECT * FROM ${TableNames.FilesTableName}")
    fun getAll(): List<FileHashModel>

    @Update
    fun updateFileHash(file: FileHashModel)

    @Insert
    fun addFileHash(file: FileHashModel)

}