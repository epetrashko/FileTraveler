package com.epetrashko.domain.usecase

import com.epetrashko.domain.repository.FilesRepository
import javax.inject.Inject

class GetFilesByRouteUseCase @Inject constructor(
    private val filesRepository: FilesRepository
) {

    suspend operator fun invoke(route: String) =
        filesRepository.getFilesByRoute(route)
}