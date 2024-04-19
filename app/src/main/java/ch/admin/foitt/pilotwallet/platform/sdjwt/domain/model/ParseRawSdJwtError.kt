package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

sealed interface ParseRawSdJwtError {
    object InvalidJwt : ParseRawSdJwtError
    object InvalidDisclosure : ParseRawSdJwtError
    data class Unexpected(val cause: Throwable?) : ParseRawSdJwtError
}
