package mvvm.kotlin.nerdery.com.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import mvvm.kotlin.nerdery.com.data.model.api.ArtistResponse
import mvvm.kotlin.nerdery.com.data.model.entity.base.ReflectsApiResponse
import mvvm.kotlin.nerdery.com.data.persistence.converter.Converters
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity
class Artist(
    @PrimaryKey
    var id: Long? = 0,
    var name: String? = "",
    var url: String? = "",
    var facebookPageUrl: String? = "",
    var imageUrl: String? = "",
    var thumbUrl: String? = "",
    var musicBrainzIdentifier: String? = "",
    var trackerCount: Int? = 0,
    var upcomingEventCount: Int? = 0,
    var favorite: Boolean = false,
    @TypeConverters(Converters::class)
    var lastTimeSearched: LocalDateTime? = null
) : ReflectsApiResponse<Artist, ArtistResponse>, Serializable {
    override fun reflectFrom(apiResponse: ArtistResponse): Artist {
        id = apiResponse.id.toLong()
        name = apiResponse.name
        url = apiResponse.url
        facebookPageUrl = apiResponse.facebookPageUrl
        imageUrl = apiResponse.imageUrl
        thumbUrl = apiResponse.thumbUrl
        musicBrainzIdentifier = apiResponse.musicBrainzIdentifier
        trackerCount = apiResponse.trackerCount
        upcomingEventCount = apiResponse.upcomingEventCount
        lastTimeSearched = LocalDateTime.now()
        return this
    }
}