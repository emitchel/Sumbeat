package mvvm.kotlin.nerdery.com.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mvvm.kotlin.nerdery.com.R
import mvvm.kotlin.nerdery.com.constants.BundleKey
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.ui.base.BaseActivity
import mvvm.kotlin.nerdery.com.ui.base.StatefulResource
import mvvm.kotlin.nerdery.com.ui.details.DetailsActivity
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : BaseActivity() {
    private lateinit var mainViewModel: MainActivityViewModel

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
                mainViewModel.currentFragment = MainActivityViewModel.CurrentFragment.ARTISTS
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
        searchAdapter?.onItemSelectedListener = object : MainActivitySearchAdapter.OnItemSelectedListener {
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
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModelImpl::class.java)
        mainViewModel.artistSearch.observe(this, Observer<StatefulResource<Artist?>> { resource ->

            snackBar?.dismiss()

            when {
                resource.state == StatefulResource.State.LOADING -> {
                    snackBar =
                            Snackbar.make(view_pager_wrapper, getString(R.string.searching), Snackbar.LENGTH_INDEFINITE)
                    snackBar?.show()
                }
                resource.state == StatefulResource.State.SUCCESS -> {
                    if (resource.hasData()) {
                        val detailsIntent = Intent(this, DetailsActivity::class.java)
                        detailsIntent.putExtra(BundleKey.ARTIST_NAME.name, resource.getData()!!.name)
                        startActivity(detailsIntent)
                    } else {
                        snackBar = Snackbar.make(
                            view_pager_wrapper,
                            getString(resource.message ?: R.string.artist_not_found), Snackbar.LENGTH_LONG
                        )
                            .setAction(R.string.ok) { snackBar?.dismiss() }
                    }
                    snackBar?.show()
                }
                resource.state == StatefulResource.State.ERROR_NETWORK -> {
                    snackBar = Snackbar.make(
                        view_pager_wrapper,
                        getString(resource.message ?: R.string.no_network_connection), Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.retry) {
                            mainViewModel.retryLastArtistSearch()
                            snackBar?.dismiss()
                        }
                    snackBar?.show()
                }
                resource.state == StatefulResource.State.ERROR_API -> {
                    snackBar = Snackbar.make(
                        view_pager_wrapper,
                        getString(resource.message ?: R.string.service_error), Snackbar.LENGTH_LONG
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
