package mvvm.kotlin.nerdery.com.ui.main

import androidx.lifecycle.LiveData
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource

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