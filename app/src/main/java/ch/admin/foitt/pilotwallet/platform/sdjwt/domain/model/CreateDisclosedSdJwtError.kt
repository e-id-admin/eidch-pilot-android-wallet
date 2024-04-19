package ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model

sealed interface CreateDisclosedSdJwtError {
    object InvalidSdJwt : CreateDisclosedSdJwtError
    data class Unexpected(val cause: Throwable?) : CreateDisclosedSdJwtError
}

internal fun ParseRawSdJwtError.toCreateDisclosedSdJwtError() =
    when (this) {
        ParseRawSdJwtError.InvalidDisclosure -> CreateDisclosedSdJwtError.InvalidSdJwt
        ParseRawSdJwtError.InvalidJwt -> CreateDisclosedSdJwtError.InvalidSdJwt
        is ParseRawSdJwtError.Unexpected -> CreateDisclosedSdJwtError.Unexpected(cause)
    }
