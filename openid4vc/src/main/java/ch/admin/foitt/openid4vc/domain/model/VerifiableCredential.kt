package ch.admin.foitt.openid4vc.domain.model

data class VerifiableCredential(
    val format: String,
    val credential: String,
    val signingKeyId: String,
)
