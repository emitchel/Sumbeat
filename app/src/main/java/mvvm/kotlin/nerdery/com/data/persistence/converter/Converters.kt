package mvvm.kotlin.nerdery.com.data.persistence.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mvvm.kotlin.nerdery.com.data.model.Offer
import mvvm.kotlin.nerdery.com.util.DateTimeUtil.defaultFormatter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {

    @TypeConverter
    fun fromOffsetDateTime(date: LocalDateTime?): String? {
        return date?.format(defaultFormatter)
    }

    @TypeConverter
    fun toOffsetDateTime(value: String?): LocalDateTime? {
        return value?.let {
            return defaultFormatter.parse(value, LocalDateTime::from)
        }
    }

    /**
     * NOTE: If there are other json lists that use strings, you'll have to define
     * another converter and specify it in a different file
     */
    @TypeConverter
    fun fromListOfStrings(lineup: List<String>?): String? {
        return lineup?.let {
            return Gson().toJson(it, object : TypeToken<List<String>>() {}.type)
        }
    }

    @TypeConverter
    fun toListOfLineup(lineupListString: String?): List<String>? {
        return Gson().fromJson(lineupListString, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromListOfOffers(offers: List<Offer>?): String? {
        return offers?.let {
            return Gson().toJson(it, object : TypeToken<List<Offer>>() {}.type)
        }
    }

    @TypeConverter
    fun toListOfOffers(offersListStringBuffer: String): List<Offer>? {
        return Gson().fromJson(offersListStringBuffer, object : TypeToken<List<Offer>>() {}.type)
    }

}