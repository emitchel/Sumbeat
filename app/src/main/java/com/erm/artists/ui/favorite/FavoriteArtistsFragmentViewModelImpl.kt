package com.erm.artists.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.repository.ArtistRepository
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.StatefulResource
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteArtistsFragmentViewModelImpl
@Inject
constructor(
    private val bandsInTownArtistRepository: ArtistRepository
) : BaseViewModel(), FavoriteArtistsFragmentViewModel {

    private val favoriteArtists = MutableLiveData<StatefulResource<List<Artist>?>>()
    override fun getFavoriteArtists(): LiveData<StatefulResource<List<Artist>?>> = favoriteArtists

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
        viewModelScope.launch {
            lastArtistIdRemovedFromFavorites = artist.id
            bandsInTownArtistRepository.deleteFavoriteArtist(artist.id!!)
        }
    }

    override fun refreshData() {
        launch {
            favoriteArtists.value = StatefulResource.with(StatefulResource.State.LOADING)
            favoriteArtists.value =
                StatefulResource.success(bandsInTownArtistRepository.getFavoriteArtists().await())
        }
    }
}