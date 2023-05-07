package com.epetrashko.filetraveler.utils

import com.epetrashko.domain.entity.FileEntity
import javax.inject.Inject

class MainSorter @Inject constructor() {

    operator fun invoke(list: List<FileEntity>, id: Int): List<FileEntity> {
        return when (id) {
            SortDirection.BY_NAME_ASC.id -> list.sortedBy { it.name }
            SortDirection.BY_NAME_DESC.id -> list.sortedByDescending { it.name }
            SortDirection.BY_SIZE_ASC.id -> list.sortedBy(::sizeSelector)
            SortDirection.BY_SIZE_DESC.id -> list.sortedByDescending(::sizeSelector)
            SortDirection.BY_DATE_OF_CREATION_ASC.id -> list.sortedBy { it.creationTimestamp }
            SortDirection.BY_DATE_OF_CREATION_DESC.id -> list.sortedByDescending { it.creationTimestamp }
            SortDirection.BY_EXTENSION_ASC.id -> list.sortedBy(::extensionSelector)
            else -> list.sortedByDescending(::extensionSelector)
        }
    }

    private fun sizeSelector(fileEntity: FileEntity) = when (fileEntity) {
        is FileEntity.Directory -> -1
        is FileEntity.File -> fileEntity.sizeInBytes
    }

    private fun extensionSelector(fileEntity: FileEntity) = when (fileEntity) {
        is FileEntity.Directory -> ""
        is FileEntity.File -> fileEntity.extension
    }

}