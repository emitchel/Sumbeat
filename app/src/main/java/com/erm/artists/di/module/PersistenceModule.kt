package com.erm.artists.di.module

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import com.erm.artists.data.persistence.BandsInTownDatabase
import javax.inject.Singleton

/**
 * All repository classes go here
 */
@Module
class PersistenceModule {
    companion object {
        private const val BANDS_IN_TOWN_DATABASE_NAME = "bandsInTown.db"
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(applicationContext)

    @Singleton
    @Provides
    fun provideBandsInTownDatabase(applicationContext: Context): BandsInTownDatabase {
        return Room.databaseBuilder(
            applicationContext.applicationContext,
            BandsInTownDatabase::class.java,
            BANDS_IN_TOWN_DATABASE_NAME
        ).build()
    }
}