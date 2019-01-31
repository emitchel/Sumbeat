package com.nerdery.artists.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import com.nerdery.artists.ui.base.BaseActivity
import com.nerdery.artists.ui.details.DetailsActivity
import com.nerdery.artists.ui.main.MainActivity

@Module
abstract class InjectorsModule {

    @ContributesAndroidInjector()
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeDetailsActivity(): DetailsActivity

}