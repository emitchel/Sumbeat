package com.erm.artists.data.repository.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.model.api.ArtistResponse
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.persistence.BandsInTownDatabase
import com.erm.artists.data.repository.helpers.DataFetchHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

/**
 * Tests the main repository and it's interactions with the network and local persistence layers
 *
 * Note: This isn't exhaustive as it's primary purpose to provide examples
 *
 * Uses Robolectric to simulate Android runtime environment (context)
 *
 * @property coroutineContext CoroutineContext - This test suite needs coroutines to interact with the repository, scope it to the lifecycle of the test
 * @property bandsInTownArtistRepository BandsInTownArtistRepository - the primary purpose of this test suite
 * @property bandsInTownApi BandsInTownApi - Stubbed webservice
 * @property bandsInTownDatabase BandsInTownDatabase - Room database that will passively be tested
 * @property sharedPreferences SharedPreferences - An instance of shared preferences
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BandsInTownArtistRepositoryTest : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        const val TEST_SHARED_PREFS = "TestSharedPrefs"
        const val ARTIST_1 = "Artist1"
        const val ARTIST_2 = "Artist2"
        const val ARTIST_3 = "Artist3"
    }

    private lateinit var bandsInTownArtistRepository: BandsInTownArtistRepository
    private lateinit var bandsInTownApi: BandsInTownApi
    private lateinit var bandsInTownDatabase: BandsInTownDatabase
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        setupDatabase(context)
        setupApi()
        setupSharedPreferences(context)

        bandsInTownArtistRepository =
            BandsInTownArtistRepository(bandsInTownApi, bandsInTownDatabase, sharedPreferences)
    }

    private fun setupSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences(TEST_SHARED_PREFS, Context.MODE_PRIVATE)
    }

    private fun setupDatabase(context: Context) {
        bandsInTownDatabase = Room.inMemoryDatabaseBuilder(
            context, BandsInTownDatabase::class.java
        ).build()

        val a1 = Artist().apply {
            name = ARTIST_1
            id = 1
        }
        val a2 = Artist().apply {
            name = ARTIST_2
            id = 2
        }
        val a3 = Artist().apply {
            name = ARTIST_3
            id = 3
        }

        val e1 = ArtistEvent().apply {
            id = 1
            artistId = 1
        }
        val e2 = ArtistEvent().apply {
            id = 2
            artistId = 1
        }
        val e3 = ArtistEvent().apply {
            id = 3
            artistId = 2
        }
        val e4 = ArtistEvent().apply {
            id = 4
            artistId = 3
        }

        //block the current thread while switching to io
        runBlocking(Dispatchers.IO) {
            bandsInTownDatabase.artistDao().insert(listOf(a1, a2, a3))
            bandsInTownDatabase.artistEventDao().insert(listOf(e1, e2, e3, e4))
        }
    }

    private fun setupApi() {
        bandsInTownApi = Mockito.mock(BandsInTownApi::class.java)
        //stubbing api responses
        Mockito.`when`(bandsInTownApi.findArtistByName(ARTIST_1)).thenReturn(async {
            Response.success(ArtistResponse(id = "1", name = ARTIST_1))
        })
        Mockito.`when`(bandsInTownApi.findArtistByName(ARTIST_2)).thenReturn(async {
            Response.success(ArtistResponse(id = "2", name = ARTIST_2))
        })
        Mockito.`when`(bandsInTownApi.findArtistByName(ARTIST_3)).thenReturn(async {
            Response.success(ArtistResponse(id = "3", name = ARTIST_3))
        })
    }

    @After
    fun tearDown() {
        bandsInTownDatabase.close()
    }

    @Test
    fun `Get Fresh Artist From API`() = runBlocking {
        val resource = bandsInTownArtistRepository.getArtistByName(ARTIST_1).await()
        val data = resource.data!!

        //fetched the correct artist
        assert(data.id == 1L) {
            "Didn't fetch correct artist"
        }

        //fetch style was as expected
        assert(resource.dataFetchStyle == DataFetchHelper.DataFetchStyle.LOCAL_FIRST_UNTIL_STALE) {
            "Fetch style is incorrect for getArtistByName"
        }

        //initial fetch should be from network
        assert(resource.dataFetchStyleResult == DataFetchHelper.DataFetchStyle.Result.NETWORK_DATA_LOCAL_STALE)
    }


    @Test
    fun `Get Cached Artist From Local Storage`() = runBlocking {
        //fetching it once to store in local cache
        bandsInTownArtistRepository.getArtistByName(ARTIST_1).await()
        val resource = bandsInTownArtistRepository.getArtistByName(ARTIST_1).await()
        assert(resource.data != null)

        //fetched the correct artist
        assert(resource.data!!.id == 1L)

        //initial fetch should be from network
        assert(resource.dataFetchStyleResult == DataFetchHelper.DataFetchStyle.Result.LOCAL_DATA_FRESH) {
            "Resulting fetch style didn't pull from local"
        }
    }

    @Test
    fun `Favorite Artist and Fetch as favorite`() = runBlocking {
        bandsInTownArtistRepository.addFavoriteArtist(1)
        //reset cache (clear cache mechanism) so we know upsert worked as expected
        sharedPreferences.edit().clear().commit()
        //get resource again
        val freshArtist = bandsInTownArtistRepository.getArtistByName(ARTIST_1).await().data!!
        assert(freshArtist.favorite) {
            "Updating artist from network didn't keep favorite column"
        }
    }

    @Test
    fun `Fetch Favorite Artists with Events`() = runBlocking {
        //add favorite
        bandsInTownArtistRepository.addFavoriteArtist(1)
        val favoriteArtistsWithEvents = bandsInTownArtistRepository.getFavoriteArtistsWithEvents().await()
        //make sure all events are tied to the artist (probably unnecessary)
        assert(favoriteArtistsWithEvents.data?.all { artistWithEvent ->
            artistWithEvent.artistEvents.all { artistEvent ->
                artistEvent.artistId == 1L
            }
        } ?: false) {
            "Not all events fetched with favorites are tied to the artist"
        }
        //make sure we have two
        assert(favoriteArtistsWithEvents.data?.first()?.artistEvents?.count() ?: 0 == 2) {
            "Didn't fetch the two events associated to this favorite artist"
        }
    }
}