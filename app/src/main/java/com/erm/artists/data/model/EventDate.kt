package com.erm.artists.data.model

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

class EventDate {
    companion object {
        //https://app.swaggerhub.com/apis/Bandsintown/PublicAPI/3.0.0#/artist%20events/artistEvents
        private const val UPCOMING = "upcoming"
        private const val PAST = "past"
        private const val ALL = "all"

        fun upcoming(): EventDate {
            return EventDate()
                .apply { value = UPCOMING }
        }

        fun past(): EventDate {
            return EventDate()
                .apply { value = PAST }
        }

        fun all(): EventDate {
            return EventDate()
                .apply { value = ALL }
        }

        fun asDateSpan(from: OffsetDateTime, to: OffsetDateTime): EventDate {
            return EventDate().withDateRange(from, to)
        }
    }

    var value: String = UPCOMING //default
        private set //private outside of class

    private fun withDateRange(from: OffsetDateTime, to: OffsetDateTime): EventDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        value = "${formatter.format(from)},${formatter.format(to)}"
        return this
    }

    override fun toString(): String {
        return value
    }
}