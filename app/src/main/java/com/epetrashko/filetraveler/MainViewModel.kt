package com.epetrashko.filetraveler

import android.util.Log
import androidx.lifecycle.ViewModel
import com.epetrashko.domain.repository.FilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filesRepository: FilesRepository
): ViewModel() {


    fun checkIsWorking() {
        Log.d("kndfjd", filesRepository.getFilesByRoute().toString())
    }


}