package com.nerdery.artists.constants

enum class CacheKey {
    ARTIST,
    ARTIST_EVENTS;

    override fun toString(): String {
        return name
    }
}