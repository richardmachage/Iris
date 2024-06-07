package com.forsythe.iris.di

import android.content.Context
import com.forsythe.iris.Application
import com.forsythe.iris.data.room.IrisDao
import com.forsythe.iris.data.room.IrisDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesApplication(@ApplicationContext appContext : Context): Application = appContext as Application

    @Provides
    @Singleton
    fun providesIrisDatabase(application: Application):IrisDatabase = IrisDatabase.getInstance(application)

    @Provides
    fun providesIrisDao(database: IrisDatabase) : IrisDao = database.irisDao()

}