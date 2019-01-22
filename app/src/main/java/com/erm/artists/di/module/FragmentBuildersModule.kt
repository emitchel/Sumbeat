package com.erm.artists.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.erm.artists.ui.favorite.FavoriteArtistsFragment
import com.erm.artists.ui.favorite.FavoriteEventsFragment
import com.erm.artists.ui.main.MainFragment

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteArtistsFragment(): FavoriteArtistsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteEventsFragment(): FavoriteEventsFragment
}