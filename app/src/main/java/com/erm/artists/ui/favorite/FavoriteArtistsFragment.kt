package com.erm.artists.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.favorite_artists_fragment.*
import com.erm.artists.R
import com.erm.artists.constants.BundleKey
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.extensions.gone
import com.erm.artists.extensions.setVisibility
import com.erm.artists.extensions.visible
import com.erm.artists.ui.base.BaseFragment
import com.erm.artists.ui.details.DetailsActivity
import com.erm.artists.util.IntentUtil

class FavoriteArtistsFragment : BaseFragment(),
    ArtistsAdapter.ArtistListener {

    companion object {
        fun newInstance(): FavoriteArtistsFragment {
            return FavoriteArtistsFragment()
        }
    }

    private lateinit var viewModel: FavoriteArtistsFragmentViewModel
    private var adapter: ArtistsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.favorite_artists_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    private fun setupObservers() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FavoriteArtistsFragmentViewModelImpl::class.java)
        viewModel.getFavoriteArtists().observe(this, Observer { artistsWithEvents ->
            if (artistsWithEvents.isSuccessful()) {
                if (artistsWithEvents.hasData() && artistsWithEvents.getData()!!.isNotEmpty()) {
                    adapter?.updateItems(artistsWithEvents.getData()!!.toMutableList()) ?: run {
                        rv_favorite_artists.layoutManager =
                                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                        adapter = ArtistsAdapter(
                            artistsWithEvents.getData()!!.toMutableList(),
                            this@FavoriteArtistsFragment
                        )
                        rv_favorite_artists.adapter = adapter
                    }
                    ll_no_favorites_wrapper.gone()
                } else {
                    adapter?.artists?.clear()
                    ll_no_favorites_wrapper.visible()
                }
            }
        })
    }

    override fun onArtistClicked(artist: Artist, imageView: ImageView) {
        val detailsIntent = Intent(activity, DetailsActivity::class.java)
        detailsIntent.putExtra(BundleKey.ARTIST_NAME.name, artist.name)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, imageView, "artistImage")
        activity?.startActivity(detailsIntent, options.toBundle())
    }

    override fun toggleFavoriteStatusForArtist(artist: Artist, position: Int) {
        adapter?.removeItem(position)
        viewModel.unFavoriteArtist(artist)
        setEmptyListMessageVisibility()
        Snackbar.make(wrapper, getString(R.string.removed_favorite, artist.name!!), Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                adapter?.addItem(artist.apply { favorite = true }, position)
                viewModel.undoFavoriteArtistRemoval()
                setEmptyListMessageVisibility()
            }
            .show()
    }

    private fun setEmptyListMessageVisibility() {
        ll_no_favorites_wrapper.setVisibility(adapter?.itemCount ?: 0 <= 0)
    }

    override fun onShareArtistClicked(artist: Artist) {
        artist.url?.let {
            IntentUtil.shareString(activity!!, it, R.string.share)
        }
    }

}