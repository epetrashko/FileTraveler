package com.epetrashko.domain.repository

import com.epetrashko.domain.entity.FileEntity
import kotlinx.coroutines.flow.StateFlow

interface FilesRepository {
    val changedFileNamesFlow: StateFlow<Set<String>>
    suspend fun getFilesByRoute(route: String): List<FileEntity>
    suspend fun traverse()
}