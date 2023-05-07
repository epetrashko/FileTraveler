package com.epetrashko.filetraveler

interface FileItemCallback {

    fun onClick(name: String, isDirectory: Boolean)

    fun onLongClick(name: String, isDirectory: Boolean)

}