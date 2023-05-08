package com.epetrashko.data.di

import com.epetrashko.data.repository.FilesRepositoryImpl
import com.epetrashko.domain.repository.FilesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun filesRepository(impl: FilesRepositoryImpl): FilesRepository

}