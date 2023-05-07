package com.epetrashko.domain.repository

import com.epetrashko.domain.entity.FileEntity

interface FilesRepository {

    suspend fun getFilesByRoute(route: String? = null): List<FileEntity>

}