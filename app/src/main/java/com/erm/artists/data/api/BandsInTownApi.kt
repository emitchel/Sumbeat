package com.erm.artists.data.api

import com.erm.artists.data.model.EventDate
import com.erm.artists.data.model.api.ArtistEventResponse
import com.erm.artists.data.model.api.ArtistResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * https://app.swaggerhub.com/apis/Bandsintown/PublicAPI/3.0.0
 */
interface BandsInTownApi {

    @GET("/artists/{artistName}")
    suspend fun findArtistByName(
        @Path("artistName") artistName: String
    ): Response<ArtistResponse>

    @GET("/artists/{artistName}/events")
    suspend fun findArtistEvents(
        @Path("artistName") artistName: String,
        @Query("date") dateValue: EventDate
    ): Response<List<ArtistEventResponse>>
}