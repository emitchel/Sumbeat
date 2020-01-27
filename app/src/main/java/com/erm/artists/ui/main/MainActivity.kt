package com.erm.artists.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.erm.artists.R
import com.erm.artists.constants.BundleKey
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.ui.base.BaseActivity
import com.erm.artists.ui.base.Result
import com.erm.artists.ui.base.StatefulResource
import com.erm.artists.ui.details.DetailsActivity
import com.google.android.material.snackbar.Snackbar
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : BaseActivity() {
    private val mainViewModel by viewModels<MainActivityViewModelImpl> { factory }

    private var searchAdapter: MainActivitySearchAdapter? = null
    private var fragmentAdapter: MainPagerAdapter? = null
    private var searchSuggestionsTimer: TimerTask? = null
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setupObservers()
        setupSearchBar()
        setupNavigation()
    }

    private fun setupNavigation() {
        fragmentAdapter = MainPagerAdapter(supportFragmentManager, this)
        view_pager.offscreenPageLimit = MainPagerAdapter.TOTAL_FRAGMENTS
        view_pager.adapter = fragmentAdapter
        view_pager.setCurrentItem(mainViewModel.currentFragment.ordinal, false)
        navigation.selectedItemId = getMenuIdOfCurrentFragment()

        navigation.setOnNavigationItemSelectedListener {
            if (search_bar.isSearchEnabled) {
                //hides the kb on selection
                search_bar.disableSearch()
            }

            if (it.itemId == R.id.navigation_artist) {
                mainViewModel.currentFragment =
                    MainActivityViewModel.CurrentFragment.ARTISTS
            } else if (it.itemId == R.id.navigation_venue) {
                mainViewModel.currentFragment = MainActivityViewModel.CurrentFragment.EVENTS
            }
            view_pager.setCurrentItem(mainViewModel.currentFragment.ordinal, false)
            true
        }
    }

    private fun setupSearchBar() {
        searchAdapter = MainActivitySearchAdapter(this, layoutInflater)
        search_bar.setCustomSuggestionAdapter(searchAdapter)
        searchAdapter?.onItemSelectedListener = object :
            MainActivitySearchAdapter.OnItemSelectedListener {
            override fun onSuggestionClicked(artist: Artist) {
                search_bar.disableSearch()
                mainViewModel.searchArtistByName(artist.name!!)
            }

        }
        search_bar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let {
                    search_bar.disableSearch()
                    mainViewModel.searchArtistByName(it.toString())
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (enabled) {
                    updateLastSearchedArtists()
                } else {
                    searchSuggestionsTimer?.cancel()
                }
            }

            override fun onButtonClicked(buttonCode: Int) {
                Timber.i("Button %d clicked", buttonCode)
            }
        })
        search_bar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (!search_bar.isSearchEnabled) return

                searchSuggestionsTimer?.cancel()

                searchSuggestionsTimer = Timer("Delaying Search filter", false).schedule(500L) {
                    runOnUiThread {
                        if (search_bar.text.isNotEmpty()) {
                            search_bar.updateLastSuggestions(search_bar.lastSuggestions.filter {
                                val artist = it as Artist
                                artist.name?.contains(search_bar.text, true) ?: false
                            })
                        } else {
                            search_bar.updateLastSuggestions(mainViewModel.lastSearchedArtists.value?.getData())
                        }
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }

    private fun setupObservers() {
        mainViewModel.artistSearch.observe(this, Observer<Result<Artist?>> { result ->

            snackBar?.dismiss()

            when (result) {
                is Result.Loading -> {
                    snackBar =
                        Snackbar.make(
                            view_pager_wrapper,
                            getString(R.string.searching),
                            Snackbar.LENGTH_INDEFINITE
                        )
                    snackBar?.show()
                }
                is Result.Success -> {
                    if (result.data != null) {
                        val detailsIntent = Intent(this, DetailsActivity::class.java)
                        detailsIntent.putExtra(
                            BundleKey.ARTIST_NAME.name,
                            result.data.name
                        )
                        startActivity(detailsIntent)
                    } else {
                        snackBar = Snackbar.make(
                            view_pager_wrapper,
                            getString(result.message ?: R.string.artist_not_found),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(R.string.ok) { snackBar?.dismiss() }
                    }
                    snackBar?.show()
                }
                is Result.NetworkError, is Result.ApiError -> {
                    snackBar = Snackbar.make(
                        view_pager_wrapper,
                        getString(result.message ?: R.string.generic_error),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.retry) {
                            mainViewModel.retryLastArtistSearch()
                            snackBar?.dismiss()
                        }
                    snackBar?.show()
                }
            }
        })

        mainViewModel.lastSearchedArtists.observe(this, Observer<StatefulResource<List<Artist>?>> {
            if (it.state == StatefulResource.State.SUCCESS && it.hasData()) {
                search_bar.lastSuggestions = it.getData()
            }
        })

        updateLastSearchedArtists()
    }

    private fun updateLastSearchedArtists() {
        mainViewModel.getLastSearchedArtists(resources.getInteger(R.integer.max_suggestions))
    }

    private fun getMenuIdOfCurrentFragment(): Int {
        if (mainViewModel.currentFragment.ordinal == 0) {
            return R.id.navigation_artist
        } else if (mainViewModel.currentFragment.ordinal == 1) {
            return R.id.navigation_venue
        }
        return R.id.navigation_artist
    }
}
