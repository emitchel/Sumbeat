package com.erm.artists.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import com.erm.artists.di.ViewModelFactory
import com.erm.artists.di.ViewModelKey
import com.erm.artists.ui.details.DetailsActivityViewModelImpl
import com.erm.artists.ui.favorite.FavoriteArtistsFragmentViewModelImpl
import com.erm.artists.ui.favorite.FavoriteEventsFragmentViewModelImpl
import com.erm.artists.ui.main.MainActivityViewModelImpl
import com.erm.artists.ui.main.MainFragmentViewModelImpl


/**
 * Module class used to inject dependencies in the ViewModels
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModelImpl::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModelImpl: MainActivityViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModelImpl::class)
    abstract fun bindMainFragmentViewModel(mainFragmentViewModelImpl: MainFragmentViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteArtistsFragmentViewModelImpl::class)
    abstract fun bindFavoriteArtistsFragmentViewModel(favoriteArtistsFragmentViewModelImpl: FavoriteArtistsFragmentViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FavoriteEventsFragmentViewModelImpl::class)
    abstract fun bindFavoriteEventsFragmentViewModel(favoriteEventsFragmentViewModelImpl: FavoriteEventsFragmentViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsActivityViewModelImpl::class)
    abstract fun bindDetailsActivityViewModelImpl(detailsActivityViewModelImpl: DetailsActivityViewModelImpl): ViewModel

}


