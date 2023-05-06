package com.epetrashko.data.di

import com.epetrashko.data.repository.FilesRepositoryImpl
import com.epetrashko.domain.repository.FilesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Singleton
    @Provides
    fun filesRepository(): FilesRepository {
        return FilesRepositoryImpl()
    }

}