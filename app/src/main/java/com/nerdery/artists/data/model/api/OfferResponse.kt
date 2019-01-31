package com.nerdery.artists.data.model.api

import com.nerdery.artists.data.model.api.base.ApiResponseObject

data class OfferResponse(
    val status: String,
    val type: String,
    val url: String
) : ApiResponseObject