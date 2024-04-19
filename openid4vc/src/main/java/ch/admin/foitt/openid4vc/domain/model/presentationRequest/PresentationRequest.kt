package ch.admin.foitt.openid4vc.domain.model.presentationRequest

import ch.admin.foitt.openid4vc.domain.model.Invitation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresentationRequest(
    @SerialName("nonce")
    val nonce: String,
    @SerialName("presentation_definition")
    val presentationDefinition: PresentationDefinition,
    @SerialName("response_uri")
    val responseUri: String,
    @SerialName("response_mode")
    val responseMode: String,
    @SerialName("state")
    val state: String?,
    @SerialName("client_metadata")
    val clientMetaData: ClientMetaData?,
) : Invitation

@Serializable
data class PresentationDefinition(
    @SerialName("id")
    val id: String,
    @SerialName("input_descriptors")
    val inputDescriptors: List<InputDescriptor>
)

@Serializable
data class InputDescriptor(
    @SerialName("constraints")
    val constraints: Constraints,
    @SerialName("format")
    val format: Format,
    @SerialName("id")
    val id: String
)

@Serializable
data class Constraints(
    @SerialName("fields")
    val fields: List<Field>
)

@Serializable
data class Field(
    @SerialName("filter")
    val filter: Filter? = null,
    @SerialName("path")
    val path: List<String>
)

@Serializable
data class Filter(
    @SerialName("pattern")
    val pattern: String,
    @SerialName("type")
    val type: String
)

@Serializable
data class Format(
    @SerialName("jwt_vc")
    val jwtVc: JwtVc
)

@Serializable
data class JwtVc(
    @SerialName("alg")
    val alg: String
)

@Serializable
data class ClientMetaData(
    @SerialName("client_name")
    val clientName: String?,
    @SerialName("logo_uri")
    val logoUri: String?,
)
