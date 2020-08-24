package com.erm.artists.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class
    ]
)
class AppModule {
    @Singleton
    @Provides
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext
}