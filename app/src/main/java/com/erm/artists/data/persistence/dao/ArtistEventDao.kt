package com.erm.artists.data.persistence.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import com.erm.artists.data.model.EventDate
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.persistence.dao.base.BaseDao


@Dao
abstract class ArtistEventDao : BaseDao<ArtistEvent>() {

    @Query("SELECT * FROM ArtistEvent WHERE artistId=:artistId")
    abstract fun getEventsByArtistId(artistId: Long): List<ArtistEvent>?

    @Query("SELECT * FROM ArtistEvent WHERE id=:eventId")
    abstract fun findEventById(eventId: Long): ArtistEvent?

    @Ignore
    fun getEventsByArtistIdAndEventDate(artistId: Long, eventDate: EventDate): List<ArtistEvent>? {
        TODO("Abstract this function and support event date search logic")
    }

    @Query("SELECT * FROM ArtistEvent WHERE favorite = 1 ORDER BY datetime(dateTime) ASC")
    abstract fun getFavoriteEvents(): List<ArtistEvent>?

    @Query("SELECT * FROM ArtistEvent WHERE artistId = :artistId AND favorite = 1")
    abstract fun getFavoriteEventsByArtist(artistId: Long): List<ArtistEvent>?

    @Query("UPDATE ArtistEvent SET favorite = 1 WHERE id=:id")
    abstract fun favoriteArtistEvent(id: Long)

    @Query("UPDATE ArtistEvent SET favorite = 0 WHERE id=:id")
    abstract fun unfavoriteArtistEvent(id: Long)

    @Transaction
    open fun upsert(objList: List<ArtistEvent>) {
        //this isn't ideal, in hindsight, should have used a junction table to keep track of favorite artists/events
        objList.forEach { event ->
            findEventById(event.id!!)?.let { existingEvent ->
                event.apply {
                    //persisting columns that aren't returned from API
                    favorite = existingEvent.favorite
                }
                delete(existingEvent)
            }
            insert(event)
        }
    }
}