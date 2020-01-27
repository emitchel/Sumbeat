package com.erm.artists.ui.favorite

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.StatefulResource
import javax.inject.Inject

class FavoriteEventsFragmentViewModelImpl
@Inject constructor(
    private val bandsInTownArtistRepository: BandsInTownArtistRepository,
    val sharedPreferences: SharedPreferences //TODO use this to get search preferences UPCOMING or ALL events
) : BaseViewModel(), FavoriteEventsFragmentViewModel {

    private val favoriteEvents: MutableLiveData<StatefulResource<List<EventWithArtist>?>> =
        MutableLiveData()

    override fun getFavoriteArtistEvents(): LiveData<StatefulResource<List<EventWithArtist>?>> =
        favoriteEvents

    override fun refreshData() {
        launch {
            favoriteEvents.value = StatefulResource.with(StatefulResource.State.LOADING)
            favoriteEvents.value = StatefulResource.success(
                bandsInTownArtistRepository.getFavoriteEventsWithArtist()
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