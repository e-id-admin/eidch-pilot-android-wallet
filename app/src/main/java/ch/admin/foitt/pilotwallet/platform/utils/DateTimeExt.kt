package ch.admin.foitt.pilotwallet.platform.utils

import android.text.format.DateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ZonedDateTime.asDayAndMonth(locale: Locale = Locale.getDefault()): String {
    val localizedPattern = DateFormat.getBestDateTimePattern(locale, "ddMMM")
    return formatPattern(localizedPattern, locale)
}

fun ZonedDateTime.asHourMinutes(locale: Locale = Locale.getDefault()): String {
    val localizedPattern = DateFormat.getBestDateTimePattern(locale, "HHmm")
    return formatPattern(localizedPattern, locale)
}

fun ZonedDateTime.asDayMonthYear(locale: Locale = Locale.getDefault()): String {
    val localizedPattern = DateFormat.getBestDateTimePattern(locale, "ddMMMyyyy")
    return formatPattern(localizedPattern, locale)
}

private fun ZonedDateTime.formatPattern(
    pattern: String,
    locale: Locale = Locale.getDefault(),
) = format(DateTimeFormatter.ofPattern(pattern, locale))
