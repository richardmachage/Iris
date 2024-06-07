package com.forsythe.iris.di

import com.forsythe.iris.Application
import com.forsythe.iris.data.room.IrisDao
import com.forsythe.iris.data.room.IrisDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesIrisDatabase(application: Application):IrisDatabase = IrisDatabase.getInstance(application)

    @Provides
    fun providesIrisDao(database: IrisDatabase) : IrisDao = database.irisDao()

}