package ch.admin.foitt.pilotwallet.platform.composables.presentation.adapter

import java.time.ZonedDateTime

fun interface GetLocalizedDateTime {
    operator fun invoke(dateTime: ZonedDateTime): String
}
