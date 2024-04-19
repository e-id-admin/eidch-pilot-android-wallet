package ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.deeplink.domain.repository.DeepLinkIntentRepository
import ch.admin.foitt.pilotwallet.platform.deeplink.domain.usecase.SetDeepLinkIntent
import javax.inject.Inject

class SetDeepLinkIntentImpl @Inject constructor(
    private val deepLinkIntentRepository: DeepLinkIntentRepository,
) : SetDeepLinkIntent {
    override fun invoke(data: String?) {
        deepLinkIntentRepository.deepLink = data
    }
}
