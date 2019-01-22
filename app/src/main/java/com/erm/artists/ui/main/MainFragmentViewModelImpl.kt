package com.erm.artists.ui.main

import com.erm.artists.data.repository.ArtistRepository
import com.erm.artists.ui.base.BaseViewModel
import javax.inject.Inject

class MainFragmentViewModelImpl
@Inject
constructor(private val bandsInTownArtistRepository: ArtistRepository) : BaseViewModel(),
    MainFragmentViewModel {

}
