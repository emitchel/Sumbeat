package com.nerdery.artists.di.module

import com.nerdery.artists.ui.favorite.FavoriteArtistsFragment
import com.nerdery.artists.ui.favorite.FavoriteEventsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeFavoriteArtistsFragment(): FavoriteArtistsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteEventsFragment(): FavoriteEventsFragment
}