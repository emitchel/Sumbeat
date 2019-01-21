package mvvm.kotlin.nerdery.com.data.model

import mvvm.kotlin.nerdery.com.data.model.api.OfferResponse
import mvvm.kotlin.nerdery.com.data.model.entity.base.ReflectsApiResponse

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