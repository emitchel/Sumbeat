package com.nerdery.artists.data.api

import kotlinx.coroutines.Deferred
import com.nerdery.artists.data.model.EventDate
import com.nerdery.artists.data.model.api.ArtistEventResponse
import com.nerdery.artists.data.model.api.ArtistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * https://app.swaggerhub.com/apis/Bandsintown/PublicAPI/3.0.0
 */
interface BandsInTownApi {

    @GET("/artists/{artistName}")
    fun findArtistByName(
        @Path("artistName") artistName: String
    ): Deferred<Response<ArtistResponse>>

    @GET("/artists/{artistName}/events")
    fun findArtistEvents(
        @Path("artistName") artistName: String,
        @Query("date") dateValue: EventDate
    ): Deferred<Response<List<ArtistEventResponse>>>
}