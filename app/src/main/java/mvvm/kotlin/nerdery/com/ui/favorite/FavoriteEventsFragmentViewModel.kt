package mvvm.kotlin.nerdery.com.ui.favorite

import androidx.lifecycle.LiveData
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent
import mvvm.kotlin.nerdery.com.data.model.relation.EventWithArtist
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource

interface FavoriteEventsFragmentViewModel {
    fun getFavoriteArtistEvents(): LiveData<StatefulResource<List<EventWithArtist>?>>
    fun refreshData()
    fun undoFavoriteEventRemoval()
    fun favoriteEvent(eventId: Long)
    fun unfavoriteEvent(eventId: Long)
}