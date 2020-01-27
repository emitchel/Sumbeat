package com.erm.artists.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erm.artists.R
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.repository.impl.BandsInTownArtistRepository
import com.erm.artists.ui.SingleLiveEvent
import com.erm.artists.ui.base.BaseViewModel
import com.erm.artists.ui.base.Result
import com.erm.artists.ui.base.StatefulResource
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class MainActivityViewModelImpl
@Inject constructor(
    private val bandsInTownArtistRepository: BandsInTownArtistRepository
) : BaseViewModel(), MainActivityViewModel {

    private val mutableLastSearchedArtists: MutableLiveData<StatefulResource<List<Artist>?>> =
        MutableLiveData()
    override val lastSearchedArtists: LiveData<StatefulResource<List<Artist>?>> =
        mutableLastSearchedArtists

    private val mutableArtistSearch: SingleLiveEvent<Result<Artist?>> =
        SingleLiveEvent()
    override val artistSearch: LiveData<Result<Artist?>> = mutableArtistSearch
    private var lastArtistNameSearched: String? = null

    override fun getLastSearchedArtists(numberOfArtists: Int) {
        launch {
            mutableLastSearchedArtists.value = StatefulResource.with(
                StatefulResource.State.SUCCESS,
                bandsInTownArtistRepository.getLastArtistsSearched(numberOfArtists)
            )
        }
    }

    override fun searchArtistByName(artistName: String) {
        launch {
            lastArtistNameSearched = artistName
            bandsInTownArtistRepository.getArtistByName(artistName.trim()).collect {
                mutableArtistSearch.value = it
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