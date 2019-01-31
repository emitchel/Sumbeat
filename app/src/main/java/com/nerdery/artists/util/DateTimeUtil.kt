package com.nerdery.artists.util

import org.threeten.bp.format.DateTimeFormatter

object DateTimeUtil {
    const val DEFAULT_DATE_TIME_FORMAT =  "yyyy-MM-dd'T'HH:mm:ss"
    const val DEFAULT_DATE_FORMAT= "MMM dd, yyyy"

    val defaultFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)
}