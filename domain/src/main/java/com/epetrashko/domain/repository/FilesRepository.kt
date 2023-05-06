package com.epetrashko.domain.repository

import com.epetrashko.domain.entity.FileEntity

interface FilesRepository {

    fun getFilesByRoute(route: String? = null): List<FileEntity>

}