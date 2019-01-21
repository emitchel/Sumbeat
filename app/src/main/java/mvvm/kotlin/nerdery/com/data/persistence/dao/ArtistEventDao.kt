package mvvm.kotlin.nerdery.com.data.persistence.dao

import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import mvvm.kotlin.nerdery.com.data.model.EventDate
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent
import mvvm.kotlin.nerdery.com.data.persistence.dao.base.BaseDao


@Dao
abstract class ArtistEventDao : BaseDao<ArtistEvent>() {

    @Query("SELECT * FROM ArtistEvent WHERE artistId=:artistId")
    abstract fun getEventsByArtistId(artistId: Long): List<ArtistEvent>?

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
    override fun upsert(objList: List<ArtistEvent>) {
        //this isn't ideal, in hindsight, should have used a junction table to keep track of favorite artists/events
        if (objList.isNotEmpty()) {
            val insertResult = insert(objList)
            val updateList = arrayListOf<ArtistEvent>()
            val favoriteEventsFromArtist = getFavoriteEventsByArtist(objList[0].artistId!!)

            for (i in insertResult.indices) {
                if (insertResult[i] == -1L) {
                    updateList.add(objList[i].apply {
                        favorite = favoriteEventsFromArtist?.find { it.id == id }?.favorite ?: false
                    })
                }
            }

            if (!updateList.isEmpty()) {
                update(updateList)
            }
        }
    }
}