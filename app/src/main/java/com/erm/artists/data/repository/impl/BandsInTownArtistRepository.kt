package com.erm.artists.data.repository.impl

import android.content.SharedPreferences
import com.erm.artists.constants.CacheKey
import com.erm.artists.data.api.BandsInTownApi
import com.erm.artists.data.model.EventDate
import com.erm.artists.data.model.api.ArtistEventResponse
import com.erm.artists.data.model.api.ArtistResponse
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.model.relation.ArtistWithEvents
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.data.persistence.BandsInTownDatabase
import com.erm.artists.data.repository.ArtistRepository
import com.erm.artists.data.repository.base.BaseRepository
import com.erm.artists.data.repository.base.Resource
import com.erm.artists.data.repository.helpers.DataFetchHelper
import com.erm.artists.util.RepositoryUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BandsInTownArtistRepository @Inject constructor(
    private val bandsInTownApi: BandsInTownApi,
    private val bandsInTownDatabase: BandsInTownDatabase,
    private val sharedPreferences: SharedPreferences
) : BaseRepository(), ArtistRepository {

    suspend fun getArtist(name: String): Artist? = withContext(Dispatchers.IO) {
        if (RepositoryUtil.isCacheStale(sharedPreferences, "artist", name, TimeUnit.HOURS.toSeconds(1))) {
            bandsInTownApi.findArtistByName(name).body()?.let {
                bandsInTownDatabase.artistDao().upsert(Artist().reflectFrom(it))
            }
            RepositoryUtil.resetCache(sharedPreferences,"artist", name)
        }

        //Single source of truth
        return@withContext bandsInTownDatabase.artistDao().findArtistByName(name)
    }

    override suspend fun getArtistByName(name: String): Resource<Artist?> {

        val dataFetchHelper = object : DataFetchHelper.LocalFirstUntilStale<Artist?>(
            "Artist",
            sharedPreferences,
            CacheKey.ARTIST.toString(),
            name,
            TimeUnit.HOURS.toSeconds(24) //storing artist information for a day
        ) {
            override suspend fun getDataFromLocal(): Artist? {
                return bandsInTownDatabase.artistDao().findArtistByName(name)
            }

            override suspend fun getDataFromNetwork(): Response<out Any?> {
                return bandsInTownApi.findArtistByName(name)
            }

            override suspend fun convertApiResponseToData(response: Response<out Any?>): Artist {
                return Artist()
                    .reflectFrom(response.body() as ArtistResponse)
            }

            override suspend fun storeFreshDataToLocal(data: Artist?): Boolean {
                data?.let {
                    bandsInTownDatabase.artistDao().upsert(data)
                    return true
                } ?: run {
                    return false
                }
            }

            override suspend fun operateOnDataPostFetch(data: Artist?) {
                data?.let {
                    //updates the last time this artist was searched
                    updateArtistSearchTime(data)
                }
            }
        }

        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun updateArtistSearchTime(artist: Artist) = withContext(ioDispatcher) {
        launch {
            artist.lastTimeSearched = LocalDateTime.now()
            bandsInTownDatabase.artistDao().upsert(artist)
        }
    }

    override suspend fun clearArtistSearchTime(artist: Artist) = withContext(ioDispatcher) {
        launch {
            artist.lastTimeSearched = null
            bandsInTownDatabase.artistDao().upsert(artist)
        }
    }

    override suspend fun getLastArtistsSearched(numberOfArtists: Int): Resource<List<Artist>?> {
        val dataFetchHelper = object : DataFetchHelper.LocalOnly<List<Artist>?>(
            "LastArtistsSearched"
        ) {
            override suspend fun getDataFromLocal(): List<Artist>? {
                return bandsInTownDatabase.artistDao().findLastArtistsSearched(numberOfArtists)
            }
        }
        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun getArtistEvents(
        artistName: String,
        eventDate: EventDate?
    ): Resource<List<ArtistEvent>?> {
        //always grabbing latest event updates
        val dataFetchHelper =
            object : DataFetchHelper.NetworkFirstLocalFailover<List<ArtistEvent>?>(
                "ArtistEvents"
            ) {

                override suspend fun getDataFromLocal(): List<ArtistEvent>? {
                    //TODO we ignoring EventDate for now, use getEventsByArtistIdAndEventDate when it's implemented
                    //First try to get the artist from local
                    val artist = bandsInTownDatabase.artistDao().findArtistByName(artistName)
                    artist?.let {
                        return bandsInTownDatabase.artistEventDao().getEventsByArtistId(it.id!!)
                    } ?: run {
                        return null //the api doesn't return artist name with events, only artist ID, BUT it takes artist name for searching...
                    }
                }

                override suspend fun getDataFromNetwork(): Response<out Any?> {
                    return bandsInTownApi.findArtistEvents(artistName, eventDate ?: EventDate())
                }

                override suspend fun convertApiResponseToData(response: Response<out Any?>): List<ArtistEvent> {
                    return (response.body() as List<ArtistEventResponse>).map {
                        ArtistEvent().reflectFrom(it)
                    }
                }

                override suspend fun storeFreshDataToLocal(data: List<ArtistEvent>?): Boolean {
                    data?.let {
                        bandsInTownDatabase.artistEventDao().upsert(data)
                        return true
                    } ?: run {
                        return false
                    }
                }
            }
        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun getFavoriteArtistsWithEvents(): Resource<List<ArtistWithEvents>?> {
        val dataFetchHelper = object : DataFetchHelper.LocalOnly<List<ArtistWithEvents>?>(
            "FavoriteArtistWithEvents"
        ) {
            override suspend fun getDataFromLocal(): List<ArtistWithEvents>? {
                return bandsInTownDatabase.artistDao().getFavoriteArtistsWithEvents()
            }
        }
        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun getFavoriteArtists(): Resource<List<Artist>?> {
        val dataFetchHelper = object : DataFetchHelper.LocalOnly<List<Artist>?>(
            "FavoriteArtists"
        ) {
            override suspend fun getDataFromLocal(): List<Artist>? {
                return bandsInTownDatabase.artistDao().getFavoriteArtists()
            }
        }
        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun addFavoriteArtist(artistId: Long) = withContext(ioDispatcher) {
        launch { bandsInTownDatabase.artistDao().favoriteArtist(artistId) }
    }

    override suspend fun deleteFavoriteArtist(artistId: Long) = withContext(ioDispatcher) {
        launch {
            bandsInTownDatabase.artistDao().unFavoriteArtist(artistId)
        }
    }

    override suspend fun getFavoriteEventsWithArtist(): Resource<List<EventWithArtist>?> {
        val dataFetchHelper = object : DataFetchHelper.LocalOnly<List<EventWithArtist>?>(
            "FavoriteArtistEvents"
        ) {
            override suspend fun getDataFromLocal(): List<EventWithArtist>? {
                val favoriteEvents = bandsInTownDatabase.artistEventDao().getFavoriteEvents()
                return favoriteEvents?.map { favoriteEvent ->
                    EventWithArtist().apply {
                        artist =
                            bandsInTownDatabase.artistDao().findArtistById(favoriteEvent.artistId!!)
                        artistEvent = favoriteEvent
                    }
                }
            }
        }
        return dataFetchHelper.fetchDataIOAsync().await()
    }

    override suspend fun addFavoriteArtistEvent(artistEventId: Long) = withContext(ioDispatcher) {
        launch {
            bandsInTownDatabase.artistEventDao().favoriteArtistEvent(artistEventId)
        }
    }

    override suspend fun deleteFavoriteArtistEvent(artistEventId: Long) =
        withContext(ioDispatcher) {
            launch {
                bandsInTownDatabase.artistEventDao().unfavoriteArtistEvent(artistEventId)
            }
        }
}