package ch.admin.foitt.pilotwallet.platform.ssi.data.source.local.mock

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Activity
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifier
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityVerifierCredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaim
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialClaimDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DisplayLanguage

object CredentialTestData {
    const val VALUE = "VALUE"
    const val KEY = "KEY"
    const val NAME1 = "NAME1"
    const val CORRECT = "CORRECT"
    const val FALLBACK = "FALLBACK"

    private const val NAME2 = "NAME2"
    private const val IDENTIFIER = "IDENTIFIER"
    private const val LOGO_DATA = "logo data"

    val credential1 = Credential(id = 1, status = CredentialStatus.VALID, createdAt = 1, updatedAt = 1)
    val credential2 = Credential(id = 2, status = CredentialStatus.VALID, createdAt = 2, updatedAt = 2)

    val credentialClaim1 = CredentialClaim(id = 1, credentialId = 1, key = KEY, value = VALUE, valueType = null)
    val credentialClaim2 = CredentialClaim(id = 2, credentialId = 2, key = KEY, value = VALUE, valueType = null)

    val credentialDisplay1 = CredentialDisplay(id = 1, credentialId = credential1.id, locale = "xx_XX", name = CORRECT)
    val credentialDisplay2 =
        CredentialDisplay(id = 2, credentialId = credential2.id, locale = DisplayLanguage.FALLBACK, name = FALLBACK)
    val credentialDisplay3 =
        CredentialDisplay(id = 3, credentialId = credential1.id, locale = DisplayLanguage.FALLBACK, name = FALLBACK)

    val credentialClaimDisplay1 = CredentialClaimDisplay(id = 1, claimId = credentialClaim1.id, name = NAME1, locale = "xx")
    val credentialClaimDisplay2 = CredentialClaimDisplay(id = 2, claimId = credentialClaim2.id, name = NAME2, locale = "xx_XX")

    val credentialIssuerDisplay1 = CredentialIssuerDisplay(id = 1, credentialId = credential1.id, name = NAME1, locale = "xx")
    val credentialIssuerDisplay2 = CredentialIssuerDisplay(id = 2, credentialId = credential2.id, name = NAME2, locale = "xx_XX")

    val credentialRaw1 = CredentialRaw(id = 1, credentialId = credential1.id, keyIdentifier = IDENTIFIER, payload = VALUE, format = VALUE)
    val credentialRaw2 = CredentialRaw(id = 2, credentialId = credential2.id, keyIdentifier = IDENTIFIER, payload = VALUE, format = VALUE)

    // credential received
    val activity1 = Activity(
        id = 1,
        credentialId = credential1.id,
        type = ActivityType.CREDENTIAL_RECEIVED,
        credentialSnapshotStatus = credential1.status,
        createdAt = 1,
    )
    // credential presentation accepted at verifier 1
    val activity2 = Activity(
        id = 2,
        credentialId = credential1.id,
        type = ActivityType.PRESENTATION_ACCEPTED,
        credentialSnapshotStatus = credential1.status,
        createdAt = 2,
    )
    // credential presentation declined at verifier 1
    val activity3 = Activity(
        id = 3,
        credentialId = credential1.id,
        type = ActivityType.PRESENTATION_DECLINED,
        credentialSnapshotStatus = credential1.status,
        createdAt = 3,
    )
    // credential presentation accepted at verifier 2
    val activity4 = Activity(
        id = 4,
        credentialId = credential1.id,
        type = ActivityType.PRESENTATION_DECLINED,
        credentialSnapshotStatus = credential1.status,
        createdAt = 4,
    )

    val activityVerifier1 = ActivityVerifier(id = 1, activityId = 2, name = NAME1, logo = LOGO_DATA)
    val activityVerifier2 = ActivityVerifier(id = 2, activityId = 3, name = NAME1, logo = LOGO_DATA)
    val activityVerifier3 = ActivityVerifier(id = 3, activityId = 4, name = NAME2, logo = null)

    val activityClaim1 = ActivityVerifierCredentialClaim(
        id = 1,
        activityVerifierId = 1,
        key = KEY,
        value = VALUE,
        valueType = null,
        order = 1,
    )
    val activityClaim2 = ActivityVerifierCredentialClaim(
        id = 2,
        activityVerifierId = 1,
        key = KEY,
        value = VALUE,
        valueType = null,
        order = 2,
    )
    val activityClaim3 = ActivityVerifierCredentialClaim(
        id = 3,
        activityVerifierId = 2,
        key = KEY,
        value = VALUE,
        valueType = null,
        order = 1,
    )

    val activityCredentialClaimDisplay1 = ActivityVerifierCredentialClaimDisplay(
        id = 1,
        activityClaimId = 1,
        name = NAME1,
        locale = "xx"
    )
    val activityCredentialClaimDisplay2 = ActivityVerifierCredentialClaimDisplay(
        id = 2,
        activityClaimId = 1,
        name = NAME2,
        locale = "xx_XX"
    )
    val activityCredentialClaimDisplay3 = ActivityVerifierCredentialClaimDisplay(
        id = 3,
        activityClaimId = 2,
        name = NAME1,
        locale = "xx"
    )
}
