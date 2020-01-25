package com.erm.artists

import android.app.Application
import com.erm.artists.di.component.AppComponent
import com.erm.artists.di.component.DaggerAppComponent
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.internal.DaggerCollections
import timber.log.Timber
import timber.log.Timber.DebugTree


class ArtistsApplication : Application() {
    lateinit var component : AppComponent
    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent
            .builder()
            // Required, see [com.erm.artists.di.component.AppComponent.Builder.application]
            .application(this)
            .build()

        //Only enable flipper if debug
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client: FlipperClient = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.start()
        }

        SoLoader.init(this, false)

        //Only enable logging if in debug
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        //Threeten DateTime initialization
        AndroidThreeTen.init(this)
    }

}