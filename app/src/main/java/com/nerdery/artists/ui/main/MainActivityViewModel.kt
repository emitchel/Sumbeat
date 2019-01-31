package com.nerdery.artists.ui.main

import androidx.lifecycle.LiveData
import com.nerdery.artists.data.model.entity.Artist
import com.nerdery.artists.ui.base.StatefulResource

interface MainActivityViewModel {
    val lastSearchedArtists: LiveData<StatefulResource<List<Artist>?>>
    fun getLastSearchedArtists(numberOfArtists: Int = 10)
    val artistSearch: LiveData<StatefulResource<Artist?>>
    fun searchArtistByName(artistName: String)
    fun clearLastSearch(artist: Artist)
    fun retryLastArtistSearch()

    var currentFragment: CurrentFragment

    enum class CurrentFragment { ARTISTS, EVENTS }
}