package com.erm.artists.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import com.erm.artists.R
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
import com.erm.artists.ui.SingleLiveEvent
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.StatefulResource
import javax.inject.Inject

class MainActivityViewModelImpl
@Inject constructor(
    private val bandsInTownArtistRepository: BandsInTownArtistRepository
) : BaseViewModel(), MainActivityViewModel {

    private val mutableLastSearchedArtists: MutableLiveData<StatefulResource<List<Artist>?>> = MutableLiveData()
    override val lastSearchedArtists: LiveData<StatefulResource<List<Artist>?>> = mutableLastSearchedArtists
    override fun getLastSearchedArtists(numberOfArtists: Int) {
        launch {

            mutableLastSearchedArtists.value = StatefulResource.with(
                StatefulResource.State.SUCCESS,
                bandsInTownArtistRepository.getLastArtistsSearched(numberOfArtists).await()
            )
        }
    }

    private val mutableArtistSearch: SingleLiveEvent<StatefulResource<Artist?>> =
        SingleLiveEvent()
    override val artistSearch: LiveData<StatefulResource<Artist?>> = mutableArtistSearch
    private var lastArtistNameSearched: String? = null
    override fun searchArtistByName(artistName: String) {
        launch {
            lastArtistNameSearched = artistName
            mutableArtistSearch.value = StatefulResource.with(StatefulResource.State.LOADING)
            val resource = bandsInTownArtistRepository.getArtistByName(artistName.trim()).await()
            when {
                resource.hasData() -> {
                    //return the value
                    mutableArtistSearch.value = StatefulResource.success(resource)
                }
                resource.isNetworkIssue() -> {
                    mutableArtistSearch.value = StatefulResource<Artist?>()
                        .apply {
                        setMessage(R.string.no_network_connection)
                        setState(StatefulResource.State.ERROR_NETWORK)
                    }
                }
                resource.isApiIssue() -> //TODO 4xx isn't necessarily a service error, expand this to sniff http code before saying service error
                    mutableArtistSearch.value = StatefulResource<Artist?>()
                        .apply {
                        setState(StatefulResource.State.ERROR_API)
                        setMessage(R.string.service_error)
                    }
                else -> mutableArtistSearch.value = StatefulResource<Artist?>()
                    .apply {
                    setState(StatefulResource.State.SUCCESS)
                    setMessage(R.string.artist_not_found)
                }
            }
        }
    }

    //If we don't want to show recently searched artists to show in drop down
    override fun clearLastSearch(artist: Artist) {
        launch {
            bandsInTownArtistRepository.clearArtistSearchTime(artist)
        }
    }

    override fun retryLastArtistSearch() {
        lastArtistNameSearched?.let {
            searchArtistByName(it)
        }
    }

    override var currentFragment = MainActivityViewModel.CurrentFragment.ARTISTS
}