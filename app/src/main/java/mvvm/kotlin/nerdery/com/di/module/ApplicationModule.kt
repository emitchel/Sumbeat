package mvvm.kotlin.nerdery.com.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import mvvm.kotlin.nerdery.com.MvvmApplication
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class ApplicationModule {

    @Singleton
    @Provides
    fun provideApplicationContext(application: MvvmApplication): Context =
            application.applicationContext

}