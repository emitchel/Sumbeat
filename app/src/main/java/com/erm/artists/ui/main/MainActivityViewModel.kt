package com.erm.artists.ui.main

import androidx.lifecycle.LiveData
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.ui.base.Result
import com.erm.artists.ui.base.StatefulResource

interface MainActivityViewModel {
    val lastSearchedArtists: LiveData<StatefulResource<List<Artist>?>>
    fun getLastSearchedArtists(numberOfArtists: Int = 10)
    val artistSearch: LiveData<Result<Artist?>>
    fun searchArtistByName(artistName: String)
    fun clearLastSearch(artist: Artist)
    fun retryLastArtistSearch()

    var currentFragment: CurrentFragment

    enum class CurrentFragment { ARTISTS, EVENTS }
}