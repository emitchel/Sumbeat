package com.erm.artists.data.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.relation.ArtistWithEvents
import com.erm.artists.data.persistence.dao.base.BaseDao

@Dao
abstract class ArtistDao : BaseDao<Artist>() {

    @Query("SELECT * FROM Artist WHERE id=:artistId")
    abstract suspend fun findArtistById(artistId: Long): Artist?

    @Query("SELECT * FROM Artist WHERE name like '%' || :artistName || '%'")
    abstract suspend fun findArtistsByName(artistName: String): List<Artist>?

    //TODO: add junction table to add if a name is searched that isn't exactly like the artist name (e.g. acdc vs AC/DC is not searchable)
    @Query("SELECT * FROM Artist WHERE name like '%' || :artistName || '%'")
    abstract suspend fun findArtistByName(artistName: String): Artist?

    @Query("SELECT * FROM Artist WHERE lastTimeSearched IS NOT NULL ORDER BY datetime(lastTimeSearched) DESC LIMIT :numberOfArtists")
    abstract suspend fun findLastArtistsSearched(numberOfArtists: Int): List<Artist>?

    @Query("SELECT * FROM Artist WHERE favorite = 1")
    abstract suspend fun getFavoriteArtists(): List<Artist>?

    @Query("UPDATE Artist SET favorite = 1 WHERE id=:id")
    abstract suspend fun favoriteArtist(id: Long)

    @Query("UPDATE Artist SET favorite = 0 WHERE id=:id")
    abstract suspend fun unFavoriteArtist(id: Long)

    @Query("SELECT * FROM Artist WHERE favorite = 1")
    abstract suspend fun getFavoriteArtistsWithEvents(): List<ArtistWithEvents>?

    @Query("DELETE FROM Artist")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM Artist")
    abstract fun getAll(): List<Artist>

    @Transaction
    open suspend fun upsert(artist: Artist) {
        //this isn't ideal, in hindsight, should have used a junction table to keep track of favorite artists/events
        findArtistById(artist.id!!)?.let { existingArtist ->
            artist.apply {
                //persisting columns that aren't returned from API
                favorite = existingArtist.favorite
            }
            delete(existingArtist)
        }
        insert(artist)
    }
}