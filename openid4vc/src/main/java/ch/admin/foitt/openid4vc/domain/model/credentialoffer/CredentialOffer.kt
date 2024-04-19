package ch.admin.foitt.openid4vc.domain.model.credentialoffer

import ch.admin.foitt.openid4vc.domain.model.Invitation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Spec: https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#name-credential-offer-parameters

@Serializable
data class CredentialOffer(
    @SerialName("credential_issuer")
    val credentialIssuer: String,
    val credentials: List<String>,
    val grants: Grant,
) : Invitation

@Serializable
data class Grant(
    @SerialName("urn:ietf:params:oauth:grant-type:pre-authorized_code")
    val preAuthorizedCode: PreAuthorizedContent? = null,
    @SerialName("urn:ietf:params:oauth:grant-type:authorized_code")
    val authorizedCode: AuthorizedContent? = null
)

@Serializable
data class PreAuthorizedContent(
    @SerialName("pre-authorized_code")
    val preAuthorizedCode: String,
    @SerialName("user_pin_required")
    val isUserPinRequired: Boolean = false
)

@Serializable
data class AuthorizedContent(
    @SerialName("issuer_state")
    val issuerState: String? = null
)
