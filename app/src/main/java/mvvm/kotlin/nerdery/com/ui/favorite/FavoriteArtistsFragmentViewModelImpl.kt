package mvvm.kotlin.nerdery.com.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.repository.ArtistRepository
import mvvm.kotlin.nerdery.com.ui.base.BaseViewModel
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource
import javax.inject.Inject

class FavoriteArtistsFragmentViewModelImpl
@Inject
constructor(
    private val bandsInTownArtistRepository: ArtistRepository
) : BaseViewModel(), FavoriteArtistsFragmentViewModel {

    private var lastArtistIdRemovedFromFavorites: Long? = null
    override fun undoFavoriteArtistRemoval() {
        lastArtistIdRemovedFromFavorites?.let {
            launch {
                bandsInTownArtistRepository.addFavoriteArtist(it)
            }
        }
        lastArtistIdRemovedFromFavorites = null
    }

    override fun unFavoriteArtist(artist: Artist) {
        lastArtistIdRemovedFromFavorites = artist.id
        launch {
            bandsInTownArtistRepository.deleteFavoriteArtist(artist.id!!)
        }
    }

    private val favoriteArtists = MutableLiveData<StatefulResource<List<Artist>?>>()
    override fun getFavoriteArtists(): LiveData<StatefulResource<List<Artist>?>> {
        return favoriteArtists
    }

    override fun refreshData() {
        launch {
            favoriteArtists.value = StatefulResource.with(StatefulResource.State.LOADING)
            favoriteArtists.value = StatefulResource.success(bandsInTownArtistRepository.getFavoriteArtists().await())
        }
    }
}