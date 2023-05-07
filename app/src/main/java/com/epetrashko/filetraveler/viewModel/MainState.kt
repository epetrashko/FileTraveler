package com.epetrashko.filetraveler.viewModel

import com.epetrashko.filetraveler.FilePresentation

sealed class MainState(open val currentRoute: String? = null) {

    data class Loading(override val currentRoute: String? = null) : MainState(currentRoute)

    data class Error(override val currentRoute: String? = null) : MainState(currentRoute)

    data class Data(override val currentRoute: String? = null, val files: List<FilePresentation>) :
        MainState(currentRoute)

}