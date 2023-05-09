package com.epetrashko.filetraveler.manager

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {

    private val mimeMap = MimeTypeMap.getSingleton()

    fun getUriForFile(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    fun getMimeTypeByExtension(extension: String?): String =
        mimeMap.getMimeTypeFromExtension(extension) ?: "*/*"

}