package com.epetrashko.filetraveler

interface FileItemCallback {

    fun onClick(file: FilePresentation)

    fun onLongClick(file: FilePresentation)

}