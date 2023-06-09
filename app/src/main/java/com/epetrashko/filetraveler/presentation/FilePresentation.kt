package com.epetrashko.filetraveler.presentation

import androidx.annotation.DrawableRes

data class FilePresentation(
    val name: String,
    val description: String,
    val path: String,
    val isDirectory: Boolean,
    @DrawableRes val icon: Int,
    val isChanged: Boolean = false
)
