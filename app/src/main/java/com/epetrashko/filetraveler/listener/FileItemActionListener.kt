package com.epetrashko.filetraveler.listener

import com.epetrashko.filetraveler.presentation.FilePresentation

interface FileItemActionListener {

    fun onClick(file: FilePresentation)

    fun onLongClick(file: FilePresentation)

}