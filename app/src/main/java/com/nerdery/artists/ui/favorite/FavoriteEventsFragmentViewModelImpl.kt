package com.nerdery.artists.ui.favorite

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.nerdery.artists.data.model.relation.EventWithArtist
import com.nerdery.artists.data.repository.impl.BandsInTownArtistRepository
import com.nerdery.artists.ui.base.BaseViewModel
import com.nerdery.artists.ui.base.StatefulResource
import javax.inject.Inject

class FavoriteEventsFragmentViewModelImpl
@Inject constructor(
    private val bandsInTownArtistRepository: BandsInTownArtistRepository,
    val sharedPreferences: SharedPreferences //TODO use this to get search preferences UPCOMING or ALL events
) : BaseViewModel(), FavoriteEventsFragmentViewModel {

    private val favoriteEvents: MutableLiveData<StatefulResource<List<EventWithArtist>?>> = MutableLiveData()

    override fun getFavoriteArtistEvents(): LiveData<StatefulResource<List<EventWithArtist>?>> = favoriteEvents

    override fun refreshData() {
        launch {
            favoriteEvents.value = StatefulResource.with(StatefulResource.State.LOADING)
            favoriteEvents.value = StatefulResource.success(
                bandsInTownArtistRepository.getFavoriteEventsWithArtist().await()
            )
        }
    }

    override fun favoriteEvent(eventId: Long) {
        launch {
            bandsInTownArtistRepository.addFavoriteArtistEvent(eventId)
        }
    }

    private var lastEventIdRemovedFromFavorites: Long? = null
    override fun undoFavoriteEventRemoval() {
        lastEventIdRemovedFromFavorites?.let {
            launch {
                bandsInTownArtistRepository.addFavoriteArtistEvent(it)
            }
        }
        lastEventIdRemovedFromFavorites = null
    }

    override fun unfavoriteEvent(eventId: Long) {
        lastEventIdRemovedFromFavorites = eventId
        launch {
            bandsInTownArtistRepository.deleteFavoriteArtistEvent(eventId)
        }
    }
}