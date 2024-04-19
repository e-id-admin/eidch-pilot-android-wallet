package ch.admin.foitt.openid4vc.domain.model.presentationRequest

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep // Nimbus library uses Gson under-the-hood, which does not play well with R8
@Serializable
internal data class VerifiablePresentation(
    @SerialName("type")
    val type: String,
    @SerialName("verifiableCredential")
    val verifiableCredential: String,
)
