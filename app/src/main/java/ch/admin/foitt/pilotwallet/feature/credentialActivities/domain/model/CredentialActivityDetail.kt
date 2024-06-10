package ch.admin.foitt.pilotwallet.feature.credentialActivities.domain.model

import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialPreview
import ch.admin.foitt.pilotwallet.platform.database.domain.model.ActivityType
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData

data class CredentialActivityDetail(
    val id: Long,
    val actor: String,
    val actorLogo: String?,
    val credentialPreview: CredentialPreview,
    val createdAt: Long,
    val type: ActivityType,
    val claims: List<CredentialClaimData>,
)
