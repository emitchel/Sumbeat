package com.nerdery.artists.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.nerdery.artists.data.model.entity.Artist
import com.nerdery.artists.data.model.entity.ArtistEvent

class ArtistWithEvents {
    @Embedded
    lateinit var artist: Artist

    @Relation(
        parentColumn = "id",
        entityColumn = "artistId",
        entity = ArtistEvent::class
    )
    lateinit var artistEvents: List<ArtistEvent>
}