package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierWithClaims

object MockActivityVerifier {
    const val ACTIVITY_VERIFIER_ID = 3L
    const val VERIFIER_NAME = "verifierName"
    const val VERIFIER_LOGO = "verifierLogo"
    val activityVerifier = ActivityVerifier(
        id = ACTIVITY_VERIFIER_ID,
        activityId = MockActivity.ACTIVITY_ID,
        name = VERIFIER_NAME,
        logo = VERIFIER_LOGO,
    )
    val activityVerifierWithClaims = ActivityVerifierWithClaims(
        verifier = activityVerifier,
        claims = MockActivityClaim.activityClaimsWithDisplays,
    )
}
