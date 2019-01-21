package mvvm.kotlin.nerdery.com.data.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.relation.ArtistWithEvents
import mvvm.kotlin.nerdery.com.data.persistence.dao.base.BaseDao

@Dao
abstract class ArtistDao : BaseDao<Artist>() {

    @Query("SELECT * FROM Artist WHERE id=:artistId")
    abstract fun findArtistById(artistId: Long): Artist?

    @Query("SELECT * FROM Artist WHERE name like '%' || :artistName || '%'")
    abstract fun findArtistsByName(artistName: String): List<Artist>?

    //TODO: add junction table to add if a name is searched that isn't exactly like the artist name (e.g. acdc vs AC/DC is not searchable)
    @Query("SELECT * FROM Artist WHERE name like '%' || :artistName || '%'")
    abstract fun findArtistByName(artistName: String): Artist?

    @Query("SELECT * FROM Artist WHERE lastTimeSearched IS NOT NULL ORDER BY datetime(lastTimeSearched) DESC LIMIT :numberOfArtists")
    abstract fun findLastArtistsSearched(numberOfArtists: Int): List<Artist>?

    @Query("SELECT * FROM Artist WHERE favorite = 1")
    abstract fun getFavoriteArtists(): List<Artist>?

    @Query("UPDATE Artist SET favorite = 1 WHERE id=:id")
    abstract fun favoriteArtist(id: Long)

    @Query("UPDATE Artist SET favorite = 0 WHERE id=:id")
    abstract fun unFavoriteArtist(id: Long)

    @Query("SELECT * FROM Artist WHERE favorite = 1")
    abstract fun getFavoriteArtistsWithEvents(): List<ArtistWithEvents>?

    @Transaction
    override fun upsert(artist: Artist) {
        //this isn't ideal, in hindsight, should have used a junction table to keep track of favorite artists/events
        val insertResult = insert(artist)

        if (insertResult == -1L) {
            val existingArtist = findArtistById(artist.id!!)
            update(artist.apply {
                favorite = existingArtist?.favorite ?: false
            })
        }
    }
}