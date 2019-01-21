package mvvm.kotlin.nerdery.com.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import mvvm.kotlin.nerdery.com.R
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent
import mvvm.kotlin.nerdery.com.data.model.relation.ArtistWithEvents
import mvvm.kotlin.nerdery.com.data.repository.base.Resource
import mvvm.kotlin.nerdery.com.extensions.isEnterKeyCode
import mvvm.kotlin.nerdery.com.ui.base.BaseFragment
import mvvm.kotlin.nerdery.com.ui.view.transformation.PicassoCircleTransformation
import mvvm.kotlin.nerdery.com.util.IntentUtil
import kotlin.coroutines.CoroutineContext

class MainFragment :
    CoroutineScope,
    BaseFragment() {
    protected lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main //This binds all launched coroutines to this managed job

    companion object {
        fun newInstance() = MainFragment()
    }

    //TODO move to VM
    private var artistResource: Resource<Artist?>? = null
    private var eventsResource: Resource<List<ArtistEvent>?>? = null
    private var favoriteArtistsWithEvents: Resource<List<ArtistWithEvents>?>? = null

    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainFragmentViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainFragmentViewModelImpl::class.java)

        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        //TODO move to VM with lifecycle listeners
        job.cancel()
    }

    private fun setupListeners() {
        btn_search.setOnClickListener { attemptToSearch() }
        et_artist_name.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode.isEnterKeyCode()) {
                attemptToSearch()
                true
            }
            false
        }

        iv_artist_image.setOnClickListener {
            viewLargerImage()
        }
    }

    private fun attemptToSearch() {
        if (validateForm()) {
            searchArtistByName()
        }
    }

    private fun validateForm(): Boolean {
        val name = getArtistSearchString()
        if (name.isBlank()) {
            et_artist_name.error = getString(R.string.name_required)
            et_artist_name.requestFocus()
            return false
        }
        return true
    }

    private fun getArtistSearchString(): String {
        return et_artist_name.text.toString().trim()
    }

    private fun searchArtistByName() {
        //TODO move this to VM scope
        launch {
            btn_search.text = getString(R.string.loading)
//            withContext(Dispatchers.IO) {
//                artistResource = bandsInTownArtistRepository.getArtistByName(getArtistSearchString())
//                if (artistResource?.hasData() == true) {
//                    Timber.i("Got everything")
//                }
//            }
//            artistResource =
//                    mainFragmentViewModel.getBandsInTownArtistRepository().getArtistByName(getArtistSearchString())
//                        .await()
//
//
//
//
//            if (artistResource?.hasData() == true) {
//                eventsResource = mainFragmentViewModel.getBandsInTownArtistRepository()
//                    .getArtistEvents(artistResource?.data?.name!!).await()
//                mainFragmentViewModel.getBandsInTownArtistRepository().addFavoriteArtist(artistResource?.data?.id!!)
//
//                favoriteArtistsWithEvents =
//                        mainFragmentViewModel.getBandsInTownArtistRepository().getFavoriteArtistsWithEvents().await()
//                setupFormWithArtist(artistResource)
//                Toast.makeText(
//                    activity,
//                    "Result Style ${artistResource?.dataFetchStyleResult?.toString()}",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else if (artistResource?.isApiIssue() == true) {
//
//                Toast.makeText(
//                    activity,
//                    "API problem: ${artistResource?.errorMessage}",
//                    Toast.LENGTH_LONG
//                ).show()
//                clearform()
//            } else if (artistResource?.isNetworkIssue() == true) {
//                Toast.makeText(
//                    activity,
//                    "Network problem and no locally stored data!",
//                    Toast.LENGTH_LONG
//                ).show()
//                clearform()
//            } else {
//                Toast.makeText(activity, "ArtistResponse ${getArtistSearchString()} not found", Toast.LENGTH_LONG)
//                    .show()
//                clearform()
//            }
            btn_search.text = getString(R.string.search)
        }
    }

    private fun setupFormWithArtist(artist: Resource<Artist?>?) {
        artist?.data?.let {
            tv_artist_name.text = it.name
            Picasso.get()
                .load(it.thumbUrl)
                .transform(PicassoCircleTransformation())
                .into(iv_artist_image)
        } ?: run {
            //TODO: not found logic
            //TODO inspect the resource object to determine the cause
            Toast.makeText(activity, "ArtistResponse ${getArtistSearchString()} not found", Toast.LENGTH_LONG).show()
        }
    }

    private fun clearform() {
        tv_artist_name.text = ""
        iv_artist_image.setImageResource(0)
    }

    private fun viewLargerImage() {
        if (artistResource?.data != null && artistResource?.data?.imageUrl?.isNotEmpty() == true) {
            IntentUtil.openWebPageFromUrl(context!!, artistResource?.data!!.imageUrl!!)
        }
    }
}
