package com.nerdery.artists.data.model.api

import com.squareup.moshi.Json
import com.nerdery.artists.data.model.api.base.ApiResponseObject

data class ArtistResponse(
    @field:Json(name = "facebook_page_url")
    val facebookPageUrl: String,
    val id: String,
    @field:Json(name = "image_url")
    val imageUrl: String,
    @field:Json(name = "mbid")
    val musicBrainzIdentifier: String,
    val name: String,
    @field:Json(name = "thumb_url")
    val thumbUrl: String,
    @field:Json(name = "tracker_count")
    val trackerCount: Int,
    @field:Json(name = "upcoming_event_count")
    val upcomingEventCount: Int,
    val url: String
) : ApiResponseObject