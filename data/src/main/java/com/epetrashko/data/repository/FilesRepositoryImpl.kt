package com.epetrashko.data.repository

import com.epetrashko.domain.repository.FilesRepository
import javax.inject.Inject

internal class FilesRepositoryImpl @Inject constructor(): FilesRepository {

    override fun getFilesByRoute(route: String): List<Any> = listOf(1, 2, 3, 4, 5)

}