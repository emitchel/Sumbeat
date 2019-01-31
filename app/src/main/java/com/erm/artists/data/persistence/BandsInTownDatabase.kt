package com.erm.artists.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.erm.artists.data.model.entity.Artist
import com.erm.artists.data.model.entity.ArtistEvent
import com.erm.artists.data.persistence.converter.Converters
import com.erm.artists.data.persistence.dao.ArtistDao
import com.erm.artists.data.persistence.dao.ArtistEventDao

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