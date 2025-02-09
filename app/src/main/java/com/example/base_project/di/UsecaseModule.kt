package com.example.base_project.di

import com.example.base_project.data.remote.repository.RequestOTPRepo
import com.example.base_project.data.remote.repository.RequestOTPRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UsecaseModule {

    @Binds
    @Singleton
    fun bindRequestOTPRepo(impl: RequestOTPRepoImpl): RequestOTPRepo

}