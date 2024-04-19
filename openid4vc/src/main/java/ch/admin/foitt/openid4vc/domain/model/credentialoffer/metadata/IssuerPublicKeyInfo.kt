package ch.admin.foitt.openid4vc.domain.model.credentialoffer.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssuerPublicKeyInfo(
    @SerialName("keys")
    val keys: List<Key>
) {
    @Serializable
    data class Key(
        @SerialName("kty")
        val kty: String,
        @SerialName("kid")
        val kid: String,
        @SerialName("crv")
        val crv: String,
        @SerialName("x")
        val x: String,
        @SerialName("y")
        val y: String
    )
}
