package com.epetrashko.filetraveler.utils

import android.content.Context
import com.epetrashko.domain.entity.FileEntity
import com.epetrashko.filetraveler.FilePresentation
import com.epetrashko.filetraveler.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class FilePresentationConverter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resourceManager: ResourceManager
) {

    private val formatter = SimpleDateFormat("dd/M/yy h:mm a")

    operator fun invoke(entity: FileEntity): FilePresentation =
        when (entity) {
            is FileEntity.File -> FilePresentation(
                name = entity.name + entity.extension,
                description = getPrettyDescription(
                    getPrettySize(bytes = entity.sizeInBytes),
                    getPrettyTime(time = entity.creationTimestamp)
                ),
                path = entity.path,
                isDirectory = false,
                icon = R.drawable.file
            )
            is FileEntity.Directory -> FilePresentation(
                name = entity.name,
                description = getPrettyDescription(
                    getPrettyAmountOfFiles(amountOfFiles = entity.filesAmount),
                    getPrettyTime(time = entity.creationTimestamp)
                ),
                path = entity.path,
                isDirectory = true,
                icon = R.drawable.folder
            )
        }

    private fun getPrettySize(bytes: Long): String {
        return android.text.format.Formatter.formatShortFileSize(context, bytes)
    }

    private fun getPrettyAmountOfFiles(amountOfFiles: Int): String =
        resourceManager.getString(R.string.anount_of_files, amountOfFiles)

    private fun getPrettyTime(time: Long): String = formatter.format(Date(time))

    private fun getPrettyDescription(firstPart: String, secondPart: String) =
        resourceManager.getString(
            R.string.file_description,
            firstPart, secondPart
        )

}