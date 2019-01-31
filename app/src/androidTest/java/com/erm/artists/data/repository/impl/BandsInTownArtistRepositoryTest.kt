package com.erm.artists.data.repository.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.persistence.BandsInTownDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.coroutines.CoroutineContext

/**
 * Focusing in on repository, passively testing Room database as well
 */
class BandsInTownArtistRepositoryTest : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bandsInTownArtistRepository: BandsInTownArtistRepository
    private lateinit var bandsInTownApi: BandsInTownApi
    private lateinit var bandsInTownDatabase: BandsInTownDatabase
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        bandsInTownDatabase = Room.inMemoryDatabaseBuilder(
            context, BandsInTownDatabase::class.java
        ).build()
        bandsInTownApi = mock(BandsInTownApi::class.java)
        sharedPreferences = mock(SharedPreferences::class.java)
        bandsInTownArtistRepository =
                BandsInTownArtistRepository(bandsInTownApi, bandsInTownDatabase, sharedPreferences)

        val a1 = Artist().apply {
            name = "FavoriteArtist"
            id = 1
            favorite = true
        }
        val a2 = Artist().apply {
            name = "Artist2"
            id = 2
        }
        val a3 = Artist().apply {
            name = "Artist3"
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

        bandsInTownDatabase.artistDao().insert(listOf(a1, a2, a3))
        bandsInTownDatabase.artistEventDao().insert(listOf(e1, e2, e3, e4))
    }

    @After
    fun tearDown() {

    }

    @Test
    fun getFavoriteArtists_isSuccessful() {
        launch {
            val favoriteArtists = bandsInTownArtistRepository.getFavoriteArtists().await()
            assert(favoriteArtists.data!![0].id!!.toInt() == 1)
        }
    }

    @Test
    fun getFavoriteArtistsWithEvents_isSuccessful() {
        launch {
            val favoriteArtistsWithEvents = bandsInTownArtistRepository.getFavoriteArtistsWithEvents().await()
            assert(favoriteArtistsWithEvents.data?.all { artistWithEvent ->
                artistWithEvent.artistEvents.all { artistEvent ->
                    artistEvent.artistId == 1L
                }
            } ?: false)
            assert(favoriteArtistsWithEvents.data?.map { it.artistEvents }?.count() == 2)
        }
    }

    @Test
    fun upsertingFavoriteArtistKeepsPersistentColumn_isSuccessful() {
        launch(Dispatchers.IO) {
            val sameArtistUpdatedFromAPI = Artist().apply {
                name = "FavoriteArtistChangedName"
                id = 1
            }
            bandsInTownDatabase.artistDao().upsert(sameArtistUpdatedFromAPI)
            val sameArtistFromDatabase = bandsInTownDatabase.artistDao().findArtistById(1)
            assert(sameArtistFromDatabase!!.id == sameArtistUpdatedFromAPI.id)
            assert(sameArtistFromDatabase.name == sameArtistUpdatedFromAPI.name)
            assert(sameArtistFromDatabase.favorite) //the favorite column isn't coming from api, must persist in upsert
        }
    }
}