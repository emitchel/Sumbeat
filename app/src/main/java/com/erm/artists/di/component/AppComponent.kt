package com.erm.artists.di.component

import android.app.Application
import com.erm.artists.ArtistsApplication
import com.erm.artists.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApiModule::class,
        AppModule::class,
        PersistenceModule::class,
        RepositoryModule::class,
        ActivityBuilderModule::class
    ]
)
interface AppComponent {

    fun inject(application: ArtistsApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}