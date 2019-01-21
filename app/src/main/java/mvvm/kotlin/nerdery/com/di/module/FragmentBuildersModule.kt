package mvvm.kotlin.nerdery.com.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mvvm.kotlin.nerdery.com.ui.favorite.FavoriteArtistsFragment
import mvvm.kotlin.nerdery.com.ui.favorite.FavoriteEventsFragment
import mvvm.kotlin.nerdery.com.ui.main.MainFragment

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteArtistsFragment(): FavoriteArtistsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteEventsFragment(): FavoriteEventsFragment
}