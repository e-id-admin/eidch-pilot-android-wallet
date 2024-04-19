package ch.admin.foitt.pilotwallet.platform.eventTracking.domain.usecase

import kotlinx.coroutines.flow.StateFlow

fun interface IsUserPrivacyPolicyAcceptedFlow {
    operator fun invoke(): StateFlow<Boolean>
}
