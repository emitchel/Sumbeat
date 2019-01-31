package com.nerdery.artists.di.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import com.nerdery.artists.NerderyArtistsApplication
import com.nerdery.artists.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApiModule::class,
    ApplicationModule::class,
    PersistenceModule::class,
    RepositoryModule::class,
    InjectorsModule::class])
interface AppComponent: AndroidInjector<NerderyArtistsApplication> {
    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<NerderyArtistsApplication>()
}