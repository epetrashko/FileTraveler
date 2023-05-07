package com.epetrashko.filetraveler.utils

import com.epetrashko.filetraveler.R
import javax.inject.Inject

class FileIconConverter @Inject constructor() {

    operator fun invoke(extension: String) =
        when (extension) {
            "png" -> R.drawable.icon_png
            "jpg" -> R.drawable.icon_jpg
            "txt" -> R.drawable.icon_txt
            "pdf" -> R.drawable.icon_pdf
            "mp3" -> R.drawable.icon_mp3
            else -> R.drawable.file
        }

}