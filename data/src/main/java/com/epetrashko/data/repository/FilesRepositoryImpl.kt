package com.epetrashko.data.repository

import android.os.Environment
import com.epetrashko.data.db.FilesDao
import com.epetrashko.data.model.FileHashModel
import com.epetrashko.data.utils.addMutably
import com.epetrashko.domain.entity.FileEntity
import com.epetrashko.domain.repository.FilesRepository
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class FilesRepositoryImpl @Inject constructor(
    private val filesDao: FilesDao
) : FilesRepository {

    private val _changedFileNamesFlow = MutableStateFlow<Set<String>>(setOf())
    override val changedFileNamesFlow: StateFlow<Set<String>>
        get() = _changedFileNamesFlow.asStateFlow()

    private var filesHashCodes = mutableMapOf<String, String>()

    override suspend fun getFilesByRoute(route: String): List<FileEntity> =
        withContext(Dispatchers.IO) {
            val filesList = mutableListOf<FileEntity>()
            val rootDirectory = File(route)
            val files = rootDirectory.listFiles()
            files?.onEach { file ->
                val path = file.absolutePath
                if (file.isDirectory) {
                    filesList.add(
                        FileEntity.Directory(
                            name = file.name,
                            path = path,
                            creationTimestamp = getFileCreationTimeStamp(path = path),
                            filesAmount = file.listFiles()?.size ?: 0
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
            filesList
        }

    override suspend fun traverse() {
        withContext(Dispatchers.IO) {
            filesDao.getAll().forEach {
                filesHashCodes[it.path] = it.hash
            }
            val queue: Queue<File> = LinkedList()
            queue.add(Environment.getExternalStorageDirectory())

            kotlin.runCatching {
                while (!queue.isEmpty()) {
                    val currentDir = queue.poll()
                    val files = currentDir?.listFiles()
                    if (files != null) {
                        for (file in files) {
                            if (file.isDirectory) {
                                queue.add(file)
                            } else {
                                val path = file.path
                                val fileHash = calculateHashOfTheFile(path)
                                if (path in filesHashCodes) {
                                    if (filesHashCodes[path] != fileHash) {
                                        filesHashCodes[path] = fileHash
                                        _changedFileNamesFlow.value =
                                            _changedFileNamesFlow.value.addMutably(path)
                                        updateFileHashCode(path = path, newHash = fileHash)
                                    }
                                } else {
                                    filesHashCodes[path] = fileHash
                                    filesDao.addFileHash(
                                        FileHashModel(
                                            path = path,
                                            hash = fileHash
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun calculateHashOfTheFile(path: String): String =
        withContext(Dispatchers.IO) {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            val md = MessageDigest.getInstance(HASH_ALGORITHM)
            FileInputStream(File(path)).use { inputStream ->
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    md.update(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            }
            val digest = md.digest()
            val hexString = StringBuilder()

            for (byte in digest) {
                hexString.append(String.format("%02x", byte))
            }

            hexString.toString()
        }

    private fun updateFileHashCode(path: String, newHash: String) {
        filesDao.updateFileHash(
            FileHashModel(
                path = path,
                hash = newHash
            )
        )
    }

    private fun getFileCreationTimeStamp(path: String): Long {
        val attributes = Files.readAttributes(Paths.get(path), BasicFileAttributes::class.java)
        val creationTime = attributes.creationTime()
        return creationTime.to(TimeUnit.MILLISECONDS)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 8192
        private const val HASH_ALGORITHM = "SHA-256"
    }

}