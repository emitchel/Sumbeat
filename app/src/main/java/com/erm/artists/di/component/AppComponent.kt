package com.erm.artists.di.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import com.erm.artists.ArtistsApplication
import com.erm.artists.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApiModule::class,
    ApplicationModule::class,
    PersistenceModule::class,
    RepositoryModule::class,
    InjectorsModule::class])
interface AppComponent: AndroidInjector<ArtistsApplication> {
    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<ArtistsApplication>()
}