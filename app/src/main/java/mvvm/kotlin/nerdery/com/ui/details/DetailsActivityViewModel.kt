package mvvm.kotlin.nerdery.com.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.relation.EventWithArtist
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource

interface DetailsActivityViewModel {
    var artistName: MutableLiveData<String?>
    val artistDetails: LiveData<StatefulResource<Artist?>>
    val artistImage: LiveData<String?>
    val artistFavorited: LiveData<Boolean>
    val artistWebpage: LiveData<String>

    val artistEventDetails: LiveData<StatefulResource<List<EventWithArtist>?>>
    fun getArtistAndEventDetails(artistName: String)

    fun toggleArtistFavoriteValue()
    fun favoriteEvent(eventId: Long)
    fun unfavoriteEvent(eventId: Long)
}