package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

import kotlinx.serialization.json.JsonElement

data class SdJwtClaim(
    val key: String,
    val value: JsonElement,
    val disclosure: String
)
