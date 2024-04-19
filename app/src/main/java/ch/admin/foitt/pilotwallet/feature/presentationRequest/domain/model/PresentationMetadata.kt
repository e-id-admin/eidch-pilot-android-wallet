package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model

import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialClaimData

data class PresentationMetadata(
    val claims: List<CredentialClaimData>,
)
