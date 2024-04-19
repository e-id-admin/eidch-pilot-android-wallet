package ch.admin.foitt.pilotwallet.platform.ssi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PresentationRequestField(
    val jsonPath: String,
    val value: String,
) {
    val key get() = jsonPath.split(".").last()
}
