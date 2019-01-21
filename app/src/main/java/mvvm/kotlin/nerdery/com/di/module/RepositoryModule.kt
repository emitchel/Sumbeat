package mvvm.kotlin.nerdery.com.di.module

import dagger.Binds
import dagger.Module
import mvvm.kotlin.nerdery.com.data.repository.ArtistRepository
import mvvm.kotlin.nerdery.com.data.repository.impl.BandsInTownArtistRepository
import javax.inject.Singleton


/**
 * All repository classes go here
 */
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBandsInTownArtistRespository(bandsInTownArtistRepository: BandsInTownArtistRepository): ArtistRepository

}