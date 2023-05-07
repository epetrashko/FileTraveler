package com.epetrashko.filetraveler

interface FileItemCallback {

    fun onClick(path: String, isDirectory: Boolean)

    fun onLongClick(path: String, isDirectory: Boolean)

}