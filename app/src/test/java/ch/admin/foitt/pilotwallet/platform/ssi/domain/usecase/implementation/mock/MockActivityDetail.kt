package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityDetail
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivity.activity
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockActivityVerifier.activityVerifierWithClaims
import ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase.implementation.mock.MockCredential.credentialDisplays

object MockActivityDetail {
    val activityDetail = ActivityDetail(
        activity = activity,
        verifier = activityVerifierWithClaims,
        credentialDisplays = credentialDisplays,
    )
}
