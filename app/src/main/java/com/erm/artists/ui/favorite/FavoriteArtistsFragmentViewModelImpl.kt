package com.erm.artists.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.repository.ArtistRepository
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.StatefulResource
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