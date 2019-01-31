package com.nerdery.artists.data.model.api

import com.nerdery.artists.data.model.api.base.ApiResponseObject

data class VenueResponse(
    val city: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val region: String
) : ApiResponseObject