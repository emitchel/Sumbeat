package com.erm.artists.util

import android.content.SharedPreferences
import com.erm.artists.constants.CacheKey
import org.threeten.bp.OffsetDateTime

object RepositoryUtil {

    fun getSecondsSinceEpoch() = OffsetDateTime.now().toEpochSecond()

    /**
     *
     * @param cacheKey String - A unique string to represent the cache, ex: GetArtist
     * @param keyDescriptor String - A string to give a secondary description ex: ACDC
     * @param cacheLengthSeconds Long - How long is the cache considered fresh (use TimeUnit.[MINUTES/HOURS/DAYS].toSeconds(x))
     * @return Boolean
     */
    fun isCacheStale(
        sharedPreferences: SharedPreferences,
        cacheKey: CacheKey,
        keyDescriptor: String? = "",
        cacheLengthSeconds: Long
    ): Boolean {
        val lastCacheCurrentSeconds = sharedPreferences.getLong(cacheKey.toString() + "_" + keyDescriptor, -1L)
        if (lastCacheCurrentSeconds == -1L) return true
        return (getSecondsSinceEpoch().minus(lastCacheCurrentSeconds)) > cacheLengthSeconds
    }

    fun resetCache(sharedPreferences: SharedPreferences, cacheKey: CacheKey, keyDescriptor: String? = "") {
        sharedPreferences
            .edit()
            .putLong(cacheKey.toString() + "_" + keyDescriptor,
                getSecondsSinceEpoch()
            )
            .apply()
    }
}