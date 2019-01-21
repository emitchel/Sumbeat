package mvvm.kotlin.nerdery.com.data.model.api

import mvvm.kotlin.nerdery.com.data.model.api.base.ApiResponseObject

data class VenueResponse(
    val city: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val region: String
) : ApiResponseObject