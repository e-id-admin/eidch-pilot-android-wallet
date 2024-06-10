package ch.admin.foitt.pilotwallet.platform.database.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ActivityDetail(
    @Embedded
    val activity: Activity,
    @Relation(
        entity = ActivityVerifier::class,
        parentColumn = "id",
        entityColumn = "activityId",
    )
    val verifier: ActivityVerifierWithClaims,
    @Relation(
        entity = CredentialDisplay::class,
        parentColumn = "credentialId",
        entityColumn = "credentialId",
    )
    val credentialDisplays: List<CredentialDisplay>,
)

data class ActivityVerifierWithClaims(
    @Embedded val verifier: ActivityVerifier,
    @Relation(
        entity = ActivityVerifierCredentialClaim::class,
        parentColumn = "id",
        entityColumn = "activityVerifierId",
    )
    val claims: List<ActivityVerifierCredentialClaimWithDisplays>,
)

data class ActivityVerifierCredentialClaimWithDisplays(
    @Embedded val claim: ActivityVerifierCredentialClaim,
    @Relation(
        entity = ActivityVerifierCredentialClaimDisplay::class,
        parentColumn = "id",
        entityColumn = "activityClaimId",
    )
    val displays: List<ActivityVerifierCredentialClaimDisplay>,
)
