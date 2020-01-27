package com.erm.artists.di.component

import android.app.Application
import com.erm.artists.ArtistsApplication
import com.erm.artists.di.module.ApiModule
import com.erm.artists.di.module.ApplicationModule
import com.erm.artists.di.module.PersistenceModule
import com.erm.artists.di.module.RepositoryModule
import com.erm.artists.ui.base.BaseActivity
import com.erm.artists.ui.base.BaseFragment
import com.erm.artists.ui.details.DetailsActivity
import com.erm.artists.ui.main.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApiModule::class,
        ApplicationModule::class,
        PersistenceModule::class,
        RepositoryModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        //"Providing" an instance of the application for modules, as opposed to the constructor approach
        //this is faster
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(application: ArtistsApplication)
    fun inject(baseActivity: BaseActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(detailsActivity: DetailsActivity)
    fun inject(baseFragment: BaseFragment)
}