package com.nerdery.artists.data.model

import com.nerdery.artists.data.model.api.OfferResponse
import com.nerdery.artists.data.model.entity.base.ReflectsApiResponse

data class Offer(
    var status: String?="",
    var type: String?="",
    var url: String?=""
) : ReflectsApiResponse<Offer, OfferResponse> {
    override fun reflectFrom(apiResponse: OfferResponse): Offer {
        status = apiResponse.status
        type = apiResponse.type
        url = apiResponse.url
        return this
    }
}