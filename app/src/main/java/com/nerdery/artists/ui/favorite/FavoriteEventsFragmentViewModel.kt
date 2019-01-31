package com.nerdery.artists.ui.favorite

import androidx.lifecycle.LiveData
import com.nerdery.artists.data.model.relation.EventWithArtist
import com.nerdery.artists.ui.base.StatefulResource

interface FavoriteEventsFragmentViewModel {
    fun getFavoriteArtistEvents(): LiveData<StatefulResource<List<EventWithArtist>?>>
    fun refreshData()
    fun undoFavoriteEventRemoval()
    fun favoriteEvent(eventId: Long)
    fun unfavoriteEvent(eventId: Long)
}