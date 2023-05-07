package com.epetrashko.filetraveler.viewModel

import android.net.Uri

sealed interface MainNews {
    object Finish: MainNews
    data class OpenFile(val mimeType: String?, val data: Uri): MainNews

}