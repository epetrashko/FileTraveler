package com.epetrashko.filetraveler.viewModel

import com.epetrashko.filetraveler.presentation.FilePresentation

sealed class MainState(open val currentRoute: String) {

    data class Loading(override val currentRoute: String) : MainState(currentRoute)

    data class Error(override val currentRoute: String) : MainState(currentRoute)

    data class Data(override val currentRoute: String, val files: List<FilePresentation>) :
        MainState(currentRoute)

}