package com.erm.artists.data.model.api

import com.erm.artists.data.model.api.base.ApiResponseObject

data class OfferResponse(
    val status: String,
    val type: String,
    val url: String
) : ApiResponseObject