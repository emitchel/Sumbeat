package com.erm.artists.di.module
//
//import dagger.Module
//import dagger.android.ContributesAndroidInjector
//import com.erm.artists.ui.base.BaseActivity
//import com.erm.artists.ui.details.DetailsActivity
//import com.erm.artists.ui.main.MainActivity
//
//@Module
//abstract class InjectorsModule {
//
//    @ContributesAndroidInjector()
//    abstract fun contributeBaseActivity(): BaseActivity
//
//    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
//    abstract fun contributeMainActivity(): MainActivity
//
//    @ContributesAndroidInjector()
//    abstract fun contributeDetailsActivity(): DetailsActivity
//
//}