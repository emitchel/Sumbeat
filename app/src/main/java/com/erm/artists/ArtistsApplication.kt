package com.erm.artists

import android.app.Application
import com.erm.artists.di.component.AppComponent
import com.erm.artists.di.AppInjector
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


class ArtistsApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    @Inject lateinit var networkFlipperPlugin : NetworkFlipperPlugin
    lateinit var component: AppComponent

    override fun onCreate() {
        super.onCreate()
//        component = DaggerAppComponent
//            .builder()
//            // Required, see [com.erm.artists.di.component.AppComponent.Builder.application]
//            .application(this)
//            .build()
//        component.inject(this)

        AppInjector.init(this)

        //TODO use injected initializers to setup versions

        SoLoader.init(this, false)
        //Only enable flipper if debug
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client: FlipperClient = AndroidFlipperClient.getInstance(this)
            client.apply {
                addPlugin(
                    InspectorFlipperPlugin(
                        this@ArtistsApplication,
                        DescriptorMapping.withDefaults()
                    )
                )
                addPlugin(DatabasesFlipperPlugin(this@ArtistsApplication))
                addPlugin(SharedPreferencesFlipperPlugin(this@ArtistsApplication))
                addPlugin(networkFlipperPlugin)
                start()
            }
        }


        //Only enable logging if in debug
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        //Threeten DateTime initialization
        AndroidThreeTen.init(this)
    }

}