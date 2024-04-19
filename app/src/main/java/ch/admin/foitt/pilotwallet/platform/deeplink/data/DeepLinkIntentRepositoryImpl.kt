package ch.admin.foitt.pilotwallet.platform.deeplink.data

import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import javax.inject.Inject

class DeepLinkIntentRepositoryImpl @Inject constructor() : DeepLinkIntentRepository {
    override var deepLink: String? = null
}
