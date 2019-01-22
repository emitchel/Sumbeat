package com.erm.artists.ui.favorite

import androidx.lifecycle.LiveData
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.ui.base.StatefulResource

interface FavoriteArtistsFragmentViewModel {
    fun getFavoriteArtists(): LiveData<StatefulResource<List<Artist>?>>
    fun unFavoriteArtist(artist: Artist)
    fun undoFavoriteArtistRemoval()
    fun refreshData()
}