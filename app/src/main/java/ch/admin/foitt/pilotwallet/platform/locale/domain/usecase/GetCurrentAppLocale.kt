package ch.admin.foitt.pilotwallet.platform.locale.domain.usecase

import java.util.Locale

interface GetCurrentAppLocale {
    operator fun invoke(): Locale
}
