package com.erm.artists.di.module

import com.erm.artists.ui.base.BaseActivity
import com.erm.artists.ui.details.DetailsActivity
import com.erm.artists.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributesDetailsActivity(): DetailsActivity

}