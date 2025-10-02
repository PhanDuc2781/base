package com.example.base_project.di

import com.example.base_project.folder.repository.FolderAudioRepository
import com.example.base_project.folder.repository.FolderAudioRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun folderAudioRepository(impl: FolderAudioRepositoryImpl): FolderAudioRepository
}