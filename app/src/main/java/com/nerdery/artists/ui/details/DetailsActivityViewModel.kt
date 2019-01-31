package com.nerdery.artists.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nerdery.artists.data.model.entity.Artist
import com.nerdery.artists.data.model.relation.EventWithArtist
import com.nerdery.artists.ui.base.StatefulResource

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