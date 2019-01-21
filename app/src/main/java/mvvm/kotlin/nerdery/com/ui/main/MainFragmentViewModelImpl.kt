package mvvm.kotlin.nerdery.com.ui.main

import mvvm.kotlin.nerdery.com.data.repository.ArtistRepository
import mvvm.kotlin.nerdery.com.ui.base.BaseViewModel
import javax.inject.Inject

class MainFragmentViewModelImpl
@Inject
constructor(private val bandsInTownArtistRepository: ArtistRepository) : BaseViewModel(), MainFragmentViewModel {

}
