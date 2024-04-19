package ch.admin.foitt.pilotwallet.platform.ssi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CompatibleCredential(
    val credentialId: Long,
    val requestedFields: List<PresentationRequestField>,
)
