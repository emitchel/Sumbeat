package com.erm.artists.data.repository

import com.erm.artists.data.model.EventDate
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.model.relation.ArtistWithEvents
import com.erm.artists.data.model.relation.EventWithArtist
import com.erm.artists.data.repository.base.Resource
import com.erm.artists.ui.base.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {
    suspend fun getArtistByName(name: String): Flow<Result<Artist?>>

    suspend fun getLastArtistsSearched(numberOfArtists: Int = 10): Resource<List<Artist>?>

    suspend fun updateArtistSearchTime(artist: Artist): Job

    suspend fun clearArtistSearchTime(artist: Artist): Job

    suspend fun getArtistEvents(
        artistName: String,
        eventDate: EventDate? = EventDate.upcoming()
    ): Resource<List<ArtistEvent>?>

    /**
     * Returning favorite artists with corresponding events (events not guaranteed unless queried before)
     * Mostly used as an example of a relation query
     */
    suspend fun getFavoriteArtistsWithEvents(): Resource<List<ArtistWithEvents>?>

    suspend fun getFavoriteArtists(): Resource<List<Artist>?>
    suspend fun addFavoriteArtist(artistId: Long): Job
    suspend fun deleteFavoriteArtist(artistId: Long): Job

    suspend fun getFavoriteEventsWithArtist(): Resource<List<EventWithArtist>?>
    suspend fun addFavoriteArtistEvent(artistEventId: Long): Job
    suspend fun deleteFavoriteArtistEvent(artistEventId: Long): Job
}