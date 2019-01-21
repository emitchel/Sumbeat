package mvvm.kotlin.nerdery.com.data.repository

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import mvvm.kotlin.nerdery.com.data.model.EventDate
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent
import mvvm.kotlin.nerdery.com.data.model.relation.ArtistWithEvents
import mvvm.kotlin.nerdery.com.data.model.relation.EventWithArtist
import mvvm.kotlin.nerdery.com.data.repository.base.Resource

interface ArtistRepository {
    suspend fun getArtistByName(name: String): Deferred<Resource<Artist?>>

    suspend fun getLastArtistsSearched(numberOfArtists: Int = 10): Deferred<Resource<List<Artist>?>>

    suspend fun updateArtistSearchTime(artist: Artist): Job

    suspend fun clearArtistSearchTime(artist: Artist): Job

    suspend fun getArtistEvents(
        artistName: String,
        eventDate: EventDate? = EventDate.upcoming()
    ): Deferred<Resource<List<ArtistEvent>?>>

    /**
     * Returning favorite artists with corresponding events (events not guaranteed unless queried before)
     * Mostly used as an example of a relation query
     */
    suspend fun getFavoriteArtistsWithEvents(): Deferred<Resource<List<ArtistWithEvents>?>>

    suspend fun getFavoriteArtists():Deferred<Resource<List<Artist>?>>
    suspend fun addFavoriteArtist(artistId: Long): Job
    suspend fun deleteFavoriteArtist(artistId: Long): Job

    suspend fun getFavoriteEventsWithArtist(): Deferred<Resource<List<EventWithArtist>?>>
    suspend fun addFavoriteArtistEvent(artistEventId: Long): Job
    suspend fun deleteFavoriteArtistEvent(artistEventId: Long): Job
}