package mvvm.kotlin.nerdery.com.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.favorite_events_fragment.*
import mvvm.kotlin.nerdery.com.R
import mvvm.kotlin.nerdery.com.constants.BundleKey
import mvvm.kotlin.nerdery.com.data.model.relation.EventWithArtist
import mvvm.kotlin.nerdery.com.extensions.gone
import mvvm.kotlin.nerdery.com.extensions.setVisibility
import mvvm.kotlin.nerdery.com.extensions.visible
import mvvm.kotlin.nerdery.com.extensions.whenAllNotNull
import mvvm.kotlin.nerdery.com.ui.base.BaseFragment
import mvvm.kotlin.nerdery.com.ui.details.DetailsActivity
import mvvm.kotlin.nerdery.com.ui.events.EventsAdapter
import mvvm.kotlin.nerdery.com.util.IntentUtil

class FavoriteEventsFragment : BaseFragment(), EventsAdapter.ArtistEventsListener {

    companion object {
        fun newInstance(): FavoriteEventsFragment {
            return FavoriteEventsFragment()
        }
    }

    private lateinit var viewModel: FavoriteEventsFragmentViewModelImpl
    private var adapter: EventsAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.favorite_events_fragment, container, false)
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FavoriteEventsFragmentViewModelImpl::class.java)
        viewModel.getFavoriteArtistEvents().observe(this, Observer { eventsWithArtist ->
            if (eventsWithArtist.isSuccessful()) {
                if (eventsWithArtist.hasData() && eventsWithArtist.getData()!!.isNotEmpty()) {
                    adapter?.updateItems(eventsWithArtist.getData()!!.toMutableList()) ?: run {
                        rv_favorite_events.layoutManager = LinearLayoutManager(this@FavoriteEventsFragment.activity)
                        adapter =
                                EventsAdapter(eventsWithArtist.getData()!!.toMutableList(), this@FavoriteEventsFragment)
                        rv_favorite_events.adapter = adapter
                    }
                    ll_no_favorites_wrapper.gone()
                } else {
                    adapter?.events?.clear()
                    ll_no_favorites_wrapper.visible()
                }
            }
        })
    }


    override fun onArtistEventClicked(artistEvent: EventWithArtist) {
        listOf(artistEvent.artistEvent?.url, activity).whenAllNotNull {
            IntentUtil.openWebPageFromUrl(activity!!, artistEvent.artistEvent?.url!!)
        }
    }

    override fun onLineupArtistClicked(artistName: String) {
        val detailsIntent = Intent(activity!!, DetailsActivity::class.java)
        detailsIntent.putExtra(BundleKey.ARTIST_NAME.name, artistName)
        startActivity(detailsIntent)
    }

    override fun toggleFavoriteStatusForEvent(eventWithArtist: EventWithArtist, position: Int) {
        eventWithArtist.artistEvent?.id?.let {
            //only favorited events are here
            adapter?.removeItem(position)
            viewModel.unfavoriteEvent(it)
            setEmptyListMessageVisibility()
            Snackbar.make(
                wrapper,
                getString(R.string.removed_favorite, eventWithArtist.artistEvent?.description),
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.undo) {
                    adapter?.addItem(eventWithArtist.apply { artistEvent?.favorite = true }, position)
                    viewModel.undoFavoriteEventRemoval()
                    setEmptyListMessageVisibility()
                }
                .show()
        }
    }

    private fun setEmptyListMessageVisibility() {
        ll_no_favorites_wrapper.setVisibility(adapter?.itemCount ?: 0 <= 0)
    }

    override fun shareEvent(artistEvent: EventWithArtist) {
        artistEvent.artistEvent?.url?.let {
            IntentUtil.shareString(activity!!, it, R.string.share)
        }
    }

    override fun onArtistImageClicked(artistEvent: EventWithArtist, imageView: ImageView) {
        val detailsIntent = Intent(activity!!, DetailsActivity::class.java)
        detailsIntent.putExtra(BundleKey.ARTIST_NAME.name, artistEvent.artist!!.name)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, imageView, "artistImage")
        activity?.startActivity(detailsIntent, options.toBundle())
    }
}