package mvvm.kotlin.nerdery.com.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.details_activity.*
import mvvm.kotlin.nerdery.com.R
import mvvm.kotlin.nerdery.com.constants.BundleKey
import mvvm.kotlin.nerdery.com.data.model.relation.EventWithArtist
import mvvm.kotlin.nerdery.com.extensions.Orientation
import mvvm.kotlin.nerdery.com.extensions.circularReveal
import mvvm.kotlin.nerdery.com.extensions.gone
import mvvm.kotlin.nerdery.com.extensions.visible
import mvvm.kotlin.nerdery.com.ui.base.BaseActivity
import mvvm.kotlin.nerdery.com.ui.events.EventsAdapter
import mvvm.kotlin.nerdery.com.util.IntentUtil


/**
 * Since the Artist is found at the MainActivity, this view model will return the stored artist, but always search
 * for the latest events
 * @property viewModel DetailsActivityViewModel
 */
class DetailsActivity : BaseActivity(), EventsAdapter.ArtistEventsListener {
    private lateinit var viewModel: DetailsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)
        setupObservers()
        //if instance state was saved, use it over the intent bundle
        setCurrentState(savedInstanceState ?: intent.extras)
        setupUI()
    }

    private fun setCurrentState(bundle: Bundle?) {
        viewModel.artistName.value = bundle?.getString(BundleKey.ARTIST_NAME.name, null)
        if (viewModel.artistName.value == null) {
            throw IllegalArgumentException("Details Activity requires Artist Name")
        }
        viewModel.getArtistAndEventDetails(viewModel.artistName.value!!)
    }

    private fun setupUI() {
        toolbar.title = viewModel.artistName.value
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_favorite.setOnClickListener {
            viewModel.toggleArtistFavoriteValue()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.details, menu)

        // return true so that the main_page_menu pop up is opened
        return true
    }

    override fun onMenuOpened(featureId: Int, menu: Menu?): Boolean {
        menu?.findItem(R.id.action_menu_favorite_or_unfavorite)?.let {
            it.title =
                    if (viewModel.artistFavorited.value!!) getString(R.string.unfavorite) else getString(R.string.favorite)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item?.itemId == R.id.action_menu_visit_website -> {
                IntentUtil.openWebPageFromUrl(this, viewModel.artistWebpage.value!!)
                true
            }
            item?.itemId == R.id.action_menu_favorite_or_unfavorite -> {
                viewModel.toggleArtistFavoriteValue()
                true
            }
            item?.itemId == android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString(BundleKey.ARTIST_NAME.name, viewModel.artistName.value!!)
    }

    private fun setupObservers() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailsActivityViewModelImpl::class.java)

        viewModel.artistFavorited.observe(this, Observer {
            if (it) {
                fab_favorite.setImageResource(R.drawable.ic_favorite_light)
            } else {
                fab_favorite.setImageResource(R.drawable.ic_non_favorite_light)
            }
        })

        viewModel.artistImage.observe(this, Observer {
            Picasso.get()
                .load(it)
                .into(backdrop)
        })

        viewModel.artistEventDetails.observe(this, Observer { artistEvents ->
            if (artistEvents.isSuccessful()) {
                if (artistEvents.hasData() && artistEvents.getData()!!.isNotEmpty()) {
                    rv_events.layoutManager = LinearLayoutManager(this@DetailsActivity)
                    rv_events.adapter = EventsAdapter(
                        artistEvents.getData()!!.toMutableList(),
                        this@DetailsActivity
                    )
                    rv_events.circularReveal(listOf(Orientation.CENTER))
                    tv_no_events.gone()
                } else {
                    tv_no_events.visible()
                }
            } else if (!artistEvents.isLoading()) {
                tv_no_events.visible()
            }
        })
    }

    override fun onArtistEventClicked(artistEvent: EventWithArtist) {
        artistEvent.artistEvent?.url?.let {
            IntentUtil.openWebPageFromUrl(this, it)
        }
    }

    override fun onLineupArtistClicked(artistName: String) {
        val detailsIntent = Intent(this, DetailsActivity::class.java)
        detailsIntent.putExtra(BundleKey.ARTIST_NAME.name, artistName)
        startActivity(detailsIntent)
    }

    override fun toggleFavoriteStatusForEvent(eventWithArtist: EventWithArtist, position: Int) {
        eventWithArtist.artistEvent?.id?.let {
            if (eventWithArtist.artistEvent?.favorite == true) {
                viewModel.unfavoriteEvent(it)
            } else {
                viewModel.favoriteEvent(it)
            }
        }
    }

    override fun shareEvent(artistEvent: EventWithArtist) {
        artistEvent.artistEvent?.url?.let {
            IntentUtil.shareString(this, it, R.string.share)
        }
    }

    override fun onArtistImageClicked(artistEvent: EventWithArtist, imageView: ImageView) {
        //no op
    }
}