package mvvm.kotlin.nerdery.com

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import mvvm.kotlin.nerdery.com.di.component.DaggerAppComponent
import timber.log.Timber

class MvvmApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<MvvmApplication> =
        DaggerAppComponent.builder().create(this@MvvmApplication)

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this)
    }

}