package com.erm.artists.ui.favorite

import androidx.lifecycle.LiveData
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.ui.base.StatefulResource

interface FavoriteEventsFragmentViewModel {
    fun getFavoriteArtistEvents(): LiveData<StatefulResource<List<EventWithArtist>?>>
    fun refreshData()
    fun undoFavoriteEventRemoval()
    fun favoriteEvent(eventId: Long)
    fun unfavoriteEvent(eventId: Long)
}