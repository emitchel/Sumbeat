package com.erm.artists

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import com.erm.artists.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class ArtistsApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<ArtistsApplication> =
        DaggerAppComponent.builder().create(this@ArtistsApplication)

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this)
    }

}