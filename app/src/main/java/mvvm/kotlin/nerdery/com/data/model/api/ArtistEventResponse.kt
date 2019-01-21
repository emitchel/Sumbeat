package mvvm.kotlin.nerdery.com.data.model.api

import com.squareup.moshi.Json
import mvvm.kotlin.nerdery.com.data.model.api.base.ApiResponseObject

data class ArtistEventResponse(
    @field:Json(name = "artist_id")
    val artistId: String,
    val datetime: String,
    val description: String,
    val id: String,
    val lineup: List<String>,
    val offers: List<OfferResponse?>?,
    @field:Json(name = "on_sale_datetime")
    val onSaleDatetime: String?,
    val url: String?,
    val venue: VenueResponse?
) : ApiResponseObject