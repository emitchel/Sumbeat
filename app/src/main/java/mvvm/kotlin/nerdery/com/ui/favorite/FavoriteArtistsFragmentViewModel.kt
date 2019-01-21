package mvvm.kotlin.nerdery.com.ui.favorite

import androidx.lifecycle.LiveData
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource

interface FavoriteArtistsFragmentViewModel {
    fun getFavoriteArtists(): LiveData<StatefulResource<List<Artist>?>>
    fun unFavoriteArtist(artist: Artist)
    fun undoFavoriteArtistRemoval()
    fun refreshData()
}