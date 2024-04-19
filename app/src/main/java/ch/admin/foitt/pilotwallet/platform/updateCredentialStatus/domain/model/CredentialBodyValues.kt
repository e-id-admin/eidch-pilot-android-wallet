package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CredentialBodyValues(
    @SerialName("vc")
    val vc: Vc,
) {
    @Serializable
    data class Vc(
        @SerialName("validFrom")
        val validFrom: String? = null,
        @SerialName("validUntil")
        val validUntil: String? = null,
        @SerialName("credentialStatus")
        val credentialStatus: List<CredentialStatus>
    ) {
        @Serializable
        data class CredentialStatus(
            @SerialName("id")
            val id: String,
            @SerialName("type")
            val type: String,
            @SerialName("statusPurpose")
            val statusPurpose: String,
            @SerialName("statusListIndex")
            val statusListIndex: String,
            @SerialName("statusListCredential")
            val statusListCredential: String
        )
    }
}
