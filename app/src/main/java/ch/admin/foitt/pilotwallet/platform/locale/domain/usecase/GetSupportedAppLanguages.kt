package ch.admin.foitt.pilotwallet.platform.locale.domain.usecase

import java.util.Locale

interface GetSupportedAppLanguages {
    operator fun invoke(): List<Locale>
}
