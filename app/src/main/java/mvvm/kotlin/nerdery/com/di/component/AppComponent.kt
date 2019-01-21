package mvvm.kotlin.nerdery.com.di.component

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import mvvm.kotlin.nerdery.com.MvvmApplication
import mvvm.kotlin.nerdery.com.di.module.*
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    ApiModule::class,
    ApplicationModule::class,
    PersistenceModule::class,
    RepositoryModule::class,
    InjectorsModule::class])
interface AppComponent: AndroidInjector<MvvmApplication> {
    @Component.Builder
    abstract class Builder: AndroidInjector.Builder<MvvmApplication>()
}