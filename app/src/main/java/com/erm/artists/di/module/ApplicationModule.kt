package com.erm.artists.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import com.erm.artists.NerderyArtistsApplication
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class ApplicationModule {

    @Singleton
    @Provides
    fun provideApplicationContext(application: NerderyArtistsApplication): Context =
            application.applicationContext

}