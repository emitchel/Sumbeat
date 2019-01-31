package com.nerdery.artists.data.model.relation

import com.nerdery.artists.data.model.entity.Artist
import com.nerdery.artists.data.model.entity.ArtistEvent

/**
 * Not a true relation (by Room's standards) but is used to join these two data models
 * Tightly coupled to the EventsAdapter
 * @property artist Artist
 * @property artistEvent ArtistEvent
 */
class EventWithArtist {
    var artist: Artist? = null
    var artistEvent: ArtistEvent? = null
}