package ch.admin.foitt.pilotwallet.platform.eventTracking.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface UserPrivacyPolicyRepository {
    val isUserPrivacyPolicyAcceptedFlow: StateFlow<Boolean>

    fun applyUserPrivacyPolicy(hasAccepted: Boolean)
}
