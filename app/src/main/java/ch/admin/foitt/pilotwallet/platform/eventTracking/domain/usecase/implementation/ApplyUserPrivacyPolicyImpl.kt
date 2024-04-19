package ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.repository.UserPrivacyPolicyRepository
import ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase.ApplyUserPrivacyPolicy
import javax.inject.Inject

class ApplyUserPrivacyPolicyImpl @Inject constructor(
    private val userPrivacyPolicyRepository: UserPrivacyPolicyRepository,
) : ApplyUserPrivacyPolicy {
    override fun invoke(hasAccepted: Boolean) {
        userPrivacyPolicyRepository.applyUserPrivacyPolicy(hasAccepted)
    }
}
