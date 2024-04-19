package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

sealed interface ParseSdJwtError {
    object InvalidJwt : ParseSdJwtError
    object InvalidDisclosure : ParseSdJwtError
    object InvalidDigestArray : ParseSdJwtError
    data class Unexpected(val cause: Throwable?) : ParseSdJwtError
}

internal fun ParseRawSdJwtError.toParseSdJwtError() = when (this) {
    ParseRawSdJwtError.InvalidDisclosure -> ParseSdJwtError.InvalidDisclosure
    ParseRawSdJwtError.InvalidJwt -> ParseSdJwtError.InvalidJwt
    is ParseRawSdJwtError.Unexpected -> ParseSdJwtError.Unexpected(cause)
}
