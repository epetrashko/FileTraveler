package com.epetrashko.domain.repository

interface FilesRepository {

    fun getFilesByRoute(route: String = ""): List<Any>

}