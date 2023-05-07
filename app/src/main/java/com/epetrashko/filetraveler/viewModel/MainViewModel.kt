package com.epetrashko.filetraveler.viewModel

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epetrashko.domain.entity.FileEntity
import com.epetrashko.domain.repository.FilesRepository
import com.epetrashko.filetraveler.utils.FilePresentationConverter
import com.epetrashko.filetraveler.utils.MainSorter
import com.epetrashko.filetraveler.utils.SortDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Stack
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@HiltViewModel
class MainViewModel @Inject constructor(
    private val filesRepository: FilesRepository,
    private val filePresentationConverter: FilePresentationConverter,
    private val mainSorter: MainSorter
) : ViewModel() {

    private var routesStack: Stack<String> = Stack()
    private val absoluteRoute: String
        get() = if (routesStack.empty()) basePATH
        else basePATH + "/" + routesStack.joinToString(separator = "/")

    private val _state = MutableStateFlow<MainState>(MainState.Loading(null))
    val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    private var currentUnprocessedFiles: List<FileEntity> = listOf()

    var sortDirection: SortDirection = SortDirection.BY_NAME_ASC
        private set

    private val mutex = Mutex()

    private val _news = MutableSharedFlow<MainNews>()
    val news: SharedFlow<MainNews>
        get() = _news.asSharedFlow()

    fun navigateTo(directoryName: String? = null, isWithPush: Boolean = true) {
        viewModelScope.launch {
            mutex.withLock {
                if (isWithPush)
                    directoryName?.let(routesStack::push)
                _state.value = MainState.Loading(absoluteRoute)
                kotlin.runCatching {
                    currentUnprocessedFiles = mainSorter(
                        list = filesRepository.getFilesByRoute(absoluteRoute),
                        id = sortDirection.id
                    )
                    currentUnprocessedFiles
                        .map(filePresentationConverter::invoke)
                }.fold(
                    onSuccess = {
                        _state.value = MainState.Data(
                            currentRoute = absoluteRoute,
                            files = it
                        )
                    },
                    onFailure = {
                        _state.value = MainState.Error(absoluteRoute)
                    }
                )
            }
        }
    }

    fun updateSortDirection(id: Int) {
        sortDirection = SortDirection.values().find { it.id == id } ?: SortDirection.BY_NAME_ASC
        navigateTo(isWithPush = false)
    }

    fun goBack() {
        viewModelScope.launch {
            if (routesStack.empty()) _news.emit(MainNews.Finish)
            else {
                routesStack.pop()
                navigateTo(directoryName = absoluteRoute, isWithPush = false)
            }
        }
    }

    fun showError() {
        _state.value = MainState.Error(_state.value.currentRoute)
    }

    fun onFileClick(name: String, isDirectory: Boolean) {
        if (isDirectory) navigateTo(name)
        else {
            // TODO open file
        }
    }

    fun onFileLongClick(name: String, isDirectory: Boolean) {
        if (isDirectory) {
            // TODO
        } else {
            // TODO share file
        }
    }

    companion object {
        private val basePATH = Environment.getExternalStorageDirectory().path
    }


}