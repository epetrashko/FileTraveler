package com.epetrashko.domain.entity

sealed class FileEntity(
    open val name: String,
    open val creationTimestamp: Long,
    open val path: String
) {

    data class File(
        override val name: String,
        val sizeInBytes: Long,
        override val creationTimestamp: Long,
        val extension: String?,
        override val path: String
    ) : FileEntity(
        name = name,
        creationTimestamp = creationTimestamp,
        path = path
    )

    data class Directory(
        override val name: String,
        override val creationTimestamp: Long,
        override val path: String,
        val filesAmount: Int
    ) : FileEntity(
        name = name,
        creationTimestamp = creationTimestamp,
        path = path
    )

}


