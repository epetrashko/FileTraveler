package com.epetrashko.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.epetrashko.data.db.TableNames

@Entity(tableName = TableNames.FilesTableName)
data class FileHashModel(
    @PrimaryKey
    val path: String,
    val hash: String
)