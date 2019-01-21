package mvvm.kotlin.nerdery.com.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent

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