package mvvm.kotlin.nerdery.com.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mvvm.kotlin.nerdery.com.ui.base.BaseActivity
import mvvm.kotlin.nerdery.com.ui.details.DetailsActivity
import mvvm.kotlin.nerdery.com.ui.main.MainActivity

@Module
abstract class InjectorsModule {

    @ContributesAndroidInjector()
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeDetailsActivity(): DetailsActivity

}