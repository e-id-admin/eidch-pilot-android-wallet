package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

import kotlinx.serialization.json.JsonElement

data class ParsedSdJwt(
    val signedJwt: String,
    val jsonWithActualValues: JsonElement
)
