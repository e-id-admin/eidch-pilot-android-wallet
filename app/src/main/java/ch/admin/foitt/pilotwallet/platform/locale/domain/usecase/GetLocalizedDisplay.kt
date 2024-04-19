package ch.admin.foitt.pilotwallet.platform.locale.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.LocalizedDisplay

interface GetLocalizedDisplay {
    operator fun <T : LocalizedDisplay> invoke(displays: Collection<T>): T?
}
