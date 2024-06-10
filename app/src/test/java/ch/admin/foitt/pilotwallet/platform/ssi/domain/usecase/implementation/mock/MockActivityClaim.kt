package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimWithDisplays
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityVerifier.ACTIVITY_VERIFIER_ID

object MockActivityClaim {
    const val ACTIVITY_CLAIM_ID_1 = 4L
    val activityClaim1 = ActivityVerifierCredentialClaim(
        id = ACTIVITY_CLAIM_ID_1,
        activityVerifierId = ACTIVITY_VERIFIER_ID,
        key = "key1",
        value = "value1",
        valueType = null,
        order = 0,
    )
    const val ACTIVITY_CLAIM_DISPLAY_ID_1 = 5L
    val activityClaimDisplay1 = ActivityVerifierCredentialClaimDisplay(
        id = ACTIVITY_CLAIM_DISPLAY_ID_1,
        activityClaimId = ACTIVITY_CLAIM_ID_1,
        name = "name1",
        locale = "locale1",
    )
    val activityClaimDisplays1 = listOf(activityClaimDisplay1)
    val activityClaimWithDisplays1 = ActivityVerifierCredentialClaimWithDisplays(
        claim = activityClaim1,
        displays = activityClaimDisplays1,
    )

    const val ACTIVITY_CLAIM_ID_2 = 6L
    val activityClaim2 = ActivityVerifierCredentialClaim(
        id = ACTIVITY_CLAIM_ID_2,
        activityVerifierId = ACTIVITY_VERIFIER_ID,
        key = "key2",
        value = "value2",
        valueType = null,
        order = 1,
    )
    const val ACTIVITY_CLAIM_DISPLAY_ID_2 = 7L
    val activityClaimDisplay2 = ActivityVerifierCredentialClaimDisplay(
        id = ACTIVITY_CLAIM_DISPLAY_ID_2,
        activityClaimId = ACTIVITY_CLAIM_ID_2,
        name = "name2",
        locale = "locale2",
    )
    val activityClaimDisplays2 = listOf(activityClaimDisplay2)
    val activityClaimWithDisplays2 = ActivityVerifierCredentialClaimWithDisplays(
        claim = activityClaim2,
        displays = activityClaimDisplays2,
    )

    val activityClaimsWithDisplays = listOf(activityClaimWithDisplays1, activityClaimWithDisplays2)
}
