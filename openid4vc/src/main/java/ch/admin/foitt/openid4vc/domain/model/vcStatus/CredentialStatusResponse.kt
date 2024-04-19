package ch.admin.foitt.openid4vc.domain.model.vcStatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CredentialStatusResponse(
    @SerialName("iss")
    val iss: String,
    @SerialName("sub")
    val sub: String,
    @SerialName("vc")
    val vc: Vc
) {
    @Serializable
    data class Vc(
        @SerialName("@context")
        val context: List<String>,
        @SerialName("id")
        val id: String,
        @SerialName("type")
        val type: List<String>,
        @SerialName("issuer")
        val issuer: String,
        @SerialName("validFrom")
        val validFrom: String,
        @SerialName("credentialSubject")
        val credentialSubject: CredentialSubject
    ) {
        @Serializable
        data class CredentialSubject(
            @SerialName("id")
            val id: String,
            @SerialName("type")
            val type: String,
            @SerialName("statusPurpose")
            val statusPurpose: String,
            @SerialName("encodedList")
            val encodedList: String
        )
    }
}
