package com.erm.artists.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.erm.artists.R
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.StatefulResource
import kotlinx.coroutines.*
import javax.inject.Inject

class DetailsActivityViewModelImpl
@Inject constructor(
    private val bandsInTownArtistRepository: BandsInTownArtistRepository
) : BaseViewModel(), DetailsActivityViewModel {
    override var artistName = MutableLiveData<String?>()

    private val mutableArtistDetails: MutableLiveData<StatefulResource<Artist?>> = MutableLiveData()
    override val artistDetails: LiveData<StatefulResource<Artist?>> = mutableArtistDetails

    private val mutableArtistEventDetails: MutableLiveData<StatefulResource<List<EventWithArtist>?>> = MutableLiveData()
    override val artistEventDetails: LiveData<StatefulResource<List<EventWithArtist>?>> = mutableArtistEventDetails

    private val mutableImage: MutableLiveData<String?> = MutableLiveData()
    override val artistImage: LiveData<String?> = mutableImage

    private val mutableFavorite: MutableLiveData<Boolean> = MutableLiveData()
    override val artistFavorited: LiveData<Boolean> = mutableFavorite

    private val mutableWebsite: MutableLiveData<String> = MutableLiveData()
    override val artistWebpage: LiveData<String> = mutableWebsite



    override fun getArtistAndEventDetails(artistName: String) {
        launch {
            fetchArtistDetails(artistName)
        }
    }

    private suspend fun fetchArtistDetails(artistName: String) {
        mutableArtistDetails.value = StatefulResource.loading()

        val artistDetailsResource = bandsInTownArtistRepository.getArtistByName(artistName)
        when {
            artistDetailsResource.hasData() -> {
                //return the value
                mutableArtistDetails.value = StatefulResource.success(artistDetailsResource)
                mutableFavorite.value = artistDetailsResource.data!!.favorite
                mutableImage.value = artistDetailsResource.data!!.imageUrl
                mutableWebsite.value = artistDetailsResource.data!!.url
            }
            artistDetailsResource.isNetworkIssue() -> {
                mutableArtistDetails.value = StatefulResource<Artist?>()
                    .apply {
                    setMessage(R.string.no_network_connection)
                    setState(StatefulResource.State.ERROR_NETWORK)
                }
            }
            artistDetailsResource.isApiIssue() ->
                mutableArtistDetails.value = StatefulResource<Artist?>()
                    .apply {
                    setState(StatefulResource.State.ERROR_API)
                    setMessage(R.string.service_error)
                }
            else -> mutableArtistDetails.value = StatefulResource<Artist?>()
                .apply {
                setState(StatefulResource.State.SUCCESS)
                setMessage(R.string.artist_not_found)
            }
        }

        //We want the artist to be passed with each event
        fetchArtistEvents(artistDetailsResource.data)
    }

    private suspend fun fetchArtistEvents(artist: Artist?) {
        mutableArtistEventDetails.value = StatefulResource.loading()

        artist?.let {
            val artistEventDetails = bandsInTownArtistRepository.getArtistEvents(artist.name!!)

            when {
                artistEventDetails.hasData() -> {
                    mutableArtistEventDetails.value = StatefulResource.success(
                        artistEventDetails.copy(
                            artistEventDetails.data
                                ?.map { artistEvent ->
                                    EventWithArtist().apply {
                                        this.artist = artist
                                        this.artistEvent = artistEvent
                                    }
                                }
                        )
                    )
                }
                artistEventDetails.isNetworkIssue() -> {
                    mutableArtistEventDetails.value = StatefulResource<List<EventWithArtist>?>()
                        .apply {
                            setMessage(R.string.no_network_connection)
                            setState(StatefulResource.State.ERROR_NETWORK)
                        }
                }
                artistEventDetails.isApiIssue() ->
                    mutableArtistEventDetails.value = StatefulResource<List<EventWithArtist>?>()
                        .apply {
                            setState(StatefulResource.State.ERROR_API)
                            setMessage(R.string.service_error)
                        }
                else -> mutableArtistEventDetails.value = StatefulResource<List<EventWithArtist>?>()
                    .apply {
                        setState(StatefulResource.State.SUCCESS)
                        setMessage(R.string.artist_not_found)
                    }
            }
        } ?: run {

            mutableArtistEventDetails.value = StatefulResource<List<EventWithArtist>?>()
                .apply {
                    setState(StatefulResource.State.SUCCESS)
                    setMessage(R.string.artist_not_found)
                }
        }
    }

    data  class holder(val i : Int, val f : Float)

    //Since there's only one artist on the details, we can toggle
    override fun toggleArtistFavoriteValue() {
        launch {
            if (mutableFavorite.value!!) {
                bandsInTownArtistRepository.deleteFavoriteArtist(artistDetails.value!!.getData()!!.id!!)
                mutableFavorite.value = false
            } else {
                bandsInTownArtistRepository.addFavoriteArtist(artistDetails.value!!.getData()!!.id!!)
                mutableFavorite.value = true
            }
        }
    }

    //since there are many events, we need to specify what the UI wants to do with what event
    override fun favoriteEvent(eventId: Long) {
        launch {
            bandsInTownArtistRepository.addFavoriteArtistEvent(eventId)
        }

    }

    override fun unfavoriteEvent(eventId: Long) {
        launch {
            bandsInTownArtistRepository.deleteFavoriteArtistEvent(eventId)
        }
    }

}