package com.epetrashko.domain.usecase

import com.epetrashko.domain.repository.FilesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

class ObserveChangedFilesUseCase @Inject constructor(
    private val repository: FilesRepository
) {

    operator fun invoke(): StateFlow<Set<String>> =
        repository.changedFileNamesFlow

}