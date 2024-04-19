package ch.admin.foitt.openid4vc.domain.model.credentialoffer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CredentialResponse(
    @SerialName("credential")
    val credential: String,
    @SerialName("format")
    val format: String,
)
