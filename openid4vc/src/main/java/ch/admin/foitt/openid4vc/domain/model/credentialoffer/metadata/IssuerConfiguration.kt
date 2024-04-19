package ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssuerConfiguration(
    @SerialName("issuer")
    val issuer: String,
    @SerialName("jwks_uri")
    val jwksUri: String,
    @SerialName("authorization_endpoint")
    val authorizationEndpoint: String,
    @SerialName("token_endpoint")
    val tokenEndpoint: String,
    @SerialName("response_types_supported")
    val responseTypesSupported: List<String>,
    @SerialName("id_token_signing_alg_values_supported")
    val supportedSigningAlgorithms: List<String>,
    @SerialName("pushed_authorization_request_endpoint")
    val pushedAuthorizationRequestEndpoint: String,
    @SerialName("request_uri_parameter_supported")
    val isRequestUriParameterSupported: Boolean,
    @SerialName("userinfo_endpoint")
    val userInfoEndpoint: String? = null,
    @SerialName("registration_endpoint")
    val registrationEndpoint: String? = null,
    @SerialName("scopes_supported")
    val scopesSupported: String? = null,
)
