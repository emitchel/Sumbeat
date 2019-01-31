package com.nerdery.artists.ui.favorite

import androidx.lifecycle.LiveData
import com.nerdery.artists.data.model.entity.Artist
import com.nerdery.artists.ui.base.StatefulResource

interface FavoriteArtistsFragmentViewModel {
    fun getFavoriteArtists(): LiveData<StatefulResource<List<Artist>?>>
    fun unFavoriteArtist(artist: Artist)
    fun undoFavoriteArtistRemoval()
    fun refreshData()
}