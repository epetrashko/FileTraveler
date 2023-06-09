package com.epetrashko.filetraveler.viewModel

import android.net.Uri

sealed interface MainNews {
    object Finish: MainNews
    object StartServiceJob: MainNews
    object ShowSortDialog: MainNews
    data class OpenFile(val mimeType: String?, val data: Uri): MainNews
    data class ShareFile(val mimeType: String?, val data: Uri): MainNews

}