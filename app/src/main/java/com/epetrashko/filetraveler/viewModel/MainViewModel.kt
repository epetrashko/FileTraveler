package com.epetrashko.filetraveler.viewModel

import androidx.lifecycle.ViewModel
import com.epetrashko.domain.repository.FilesRepository
import com.epetrashko.filetraveler.utils.FilePresentationConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    private val filePresentationConverter: FilePresentationConverter
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading(null))
    val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    fun navigateTo(route: String? = null) {
        _state.value = MainState.Loading(route)
        kotlin.runCatching {
            filesRepository.getFilesByRoute(route)
                .map(filePresentationConverter::invoke)
        }.fold(
            onSuccess = {
                _state.value = MainState.Data(
                    currentRoute = route,
                    files = it
                )
            },
            onFailure = {
                _state.value = MainState.Error(route)
            }
        )
    }

    fun showError() {
        _state.value = MainState.Error(_state.value.currentRoute)
    }

    fun onFileClick(path: String, isDirectory: Boolean) {
        if (isDirectory) navigateTo(path)
        else {
            // TODO open file
        }
    }

    fun onFileLongClick(path: String, isDirectory: Boolean) {
        if (isDirectory) {
            // TODO
        } else {
            // TODO share file
        }
    }




}