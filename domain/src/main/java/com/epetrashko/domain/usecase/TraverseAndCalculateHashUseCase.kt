package com.epetrashko.domain.usecase

import com.epetrashko.domain.repository.FilesRepository
import javax.inject.Inject

class TraverseAndCalculateHashUseCase @Inject constructor(
    private val repository: FilesRepository
) {

    suspend operator fun invoke() =
        repository.traverse()

}