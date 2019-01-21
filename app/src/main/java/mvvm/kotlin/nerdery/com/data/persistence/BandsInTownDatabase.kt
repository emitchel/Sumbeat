package mvvm.kotlin.nerdery.com.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mvvm.kotlin.nerdery.com.data.model.entity.Artist
import mvvm.kotlin.nerdery.com.data.model.entity.ArtistEvent
import mvvm.kotlin.nerdery.com.data.persistence.converter.Converters
import mvvm.kotlin.nerdery.com.data.persistence.dao.ArtistDao
import mvvm.kotlin.nerdery.com.data.persistence.dao.ArtistEventDao

@Database(
    version = 1,
    entities = [Artist::class,
        ArtistEvent::class]
)
@TypeConverters(Converters::class)
abstract class BandsInTownDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun artistEventDao(): ArtistEventDao
}