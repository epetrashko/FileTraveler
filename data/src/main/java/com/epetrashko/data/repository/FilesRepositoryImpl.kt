package com.epetrashko.data.repository

import android.os.Environment
import com.epetrashko.domain.entity.FileEntity
import com.epetrashko.domain.repository.FilesRepository
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class FilesRepositoryImpl @Inject constructor() : FilesRepository {

    override fun getFilesByRoute(route: String?): List<FileEntity> {
        val filesList = mutableListOf<FileEntity>()
        val rootDirectory = File(route ?: Environment.getExternalStorageDirectory().path)
        val files = rootDirectory.listFiles()
        files?.onEach { file ->
            val path = file.absolutePath
            if (file.isDirectory) {
                filesList.add(
                    FileEntity.Directory(
                        name = file.name,
                        path = path,
                        creationTimestamp = getFileCreationTimeStamp(path = path)
                    )
                )
            } else {
                filesList.add(
                    FileEntity.File(
                        name = file.name,
                        extension = file.extension,
                        creationTimestamp = getFileCreationTimeStamp(path = path),
                        path = path,
                        sizeInBytes = file.length()
                    )
                )
            }
        }
        return filesList
    }

    private fun getFileCreationTimeStamp(path: String): Long {
        val attributes = Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
        val creationTime = attributes.creationTime()
        return creationTime.to(TimeUnit.MILLISECONDS)
    }

}