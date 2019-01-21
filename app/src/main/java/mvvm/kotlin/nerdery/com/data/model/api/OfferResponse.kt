package mvvm.kotlin.nerdery.com.data.model.api

import mvvm.kotlin.nerdery.com.data.model.api.base.ApiResponseObject

data class OfferResponse(
    val status: String,
    val type: String,
    val url: String
) : ApiResponseObject