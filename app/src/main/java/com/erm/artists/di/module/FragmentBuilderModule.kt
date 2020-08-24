package com.erm.artists.di.module

import com.erm.artists.ui.favorite.FavoriteArtistsFragment
import com.erm.artists.ui.favorite.FavoriteEventsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributesFavoriteArtistsFragment(): FavoriteArtistsFragment

    @ContributesAndroidInjector
    abstract fun contributesFavoriteEventsFragment(): FavoriteEventsFragment

}