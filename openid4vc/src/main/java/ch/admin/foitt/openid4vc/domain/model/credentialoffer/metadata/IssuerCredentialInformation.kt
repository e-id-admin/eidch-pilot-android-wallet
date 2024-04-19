package ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata

import ch.admin.foitt.openid4vc.domain.model.JsonAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private typealias CredentialIdentifier = String

@Serializable
data class IssuerCredentialInformation(
    @SerialName("credential_endpoint")
    val credentialEndpoint: String,
    @SerialName("credential_issuer")
    val credentialIssuer: String,
    @SerialName("credentials_supported")
    val supportedCredentials: Map<CredentialIdentifier, SupportedCredential>,
    @SerialName("display")
    val display: List<Display>
)

@Serializable
data class SupportedCredential(
    @SerialName("credential_definition")
    val credentialDefinition: CredentialDefinition,
    @SerialName("cryptographic_suites_supported")
    val cryptographicSuitesSupported: List<String>,
    @SerialName("cryptographic_binding_methods_supported")
    val cryptographicBindingMethodsSupported: List<String>? = null,
    @SerialName("format")
    val format: String,
    @SerialName("proof_types_supported")
    val proofTypesSupported: List<String>,
    @SerialName("display")
    val display: List<Display>? = null,
    @SerialName("order")
    val order: List<String>? = null
)

@Serializable
data class CredentialDefinition(
    @Serializable(with = JsonAsStringSerializer::class)
    @SerialName("credentialSubject")
    val credentialSubject: String,
    @SerialName("type")
    val type: List<String>
)

/**
 * This is the last leaf in a [CredentialDefinition.credentialSubject] branch
 */
@Serializable
data class CredentialSubject(
    @SerialName("mandatory")
    val mandatory: Boolean? = false,
    @SerialName("value_type")
    val valueType: String? = "string",
    @SerialName("display")
    val display: List<Display>? = null
)

// https://openid.net/specs/openid-4-verifiable-credential-issuance-1_0.html#section-10.2.3.1
@Serializable
data class Display(
    @SerialName("locale")
    val locale: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("logo")
    val logo: Logo? = null,
    @SerialName("name")
    val name: String,
    @SerialName("background_color")
    val backgroundColor: String? = null,
    @SerialName("text_color")
    val textColor: String? = null,
)

@Serializable
data class Logo(
    @SerialName("url")
    val url: String? = null,
    @SerialName("alt_text")
    val altText: String? = null,
    @SerialName("data")
    val data: String? = null,
)
