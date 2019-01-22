package com.erm.artists.di.module

import dagger.Binds
import dagger.Module
import com.erm.artists.data.repository.ArtistRepository
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
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