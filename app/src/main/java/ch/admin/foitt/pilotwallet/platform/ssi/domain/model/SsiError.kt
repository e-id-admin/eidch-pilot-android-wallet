@file:Suppress("TooManyFunctions")

package ch.admin.foitt.pilotwallet.platform.ssi.domain.model

import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseSdJwtError
import timber.log.Timber

interface SsiError {
    data class Unexpected(val cause: Throwable?) :
        CredentialClaimDisplayRepositoryError,
        CredentialClaimRepositoryError,
        CredentialDisplayRepositoryError,
        CredentialIssuerDisplayRepositoryError,
        CredentialIssuerRepositoryError,
        CredentialRawRepositoryError,
        CredentialRepositoryError,
        GetCompatibleCredentialsError,
        GetAllCredentialBodiesError,
        GetAllCredentialsError,
        GetCredentialByIdError,
        GetCredentialBodiesByCredentialIdError,
        DeleteCredentialError,
        GetCredentialClaimsError,
        GetCredentialClaimDisplayError,
        GetCredentialClaimDataError,
        MapToCredentialClaimDataError
}

sealed interface CredentialClaimDisplayRepositoryError
sealed interface CredentialClaimRepositoryError
sealed interface CredentialDisplayRepositoryError
sealed interface CredentialIssuerDisplayRepositoryError
sealed interface CredentialIssuerRepositoryError
sealed interface CredentialRawRepositoryError
sealed interface CredentialRepositoryError
sealed interface GetCompatibleCredentialsError
sealed interface GetAllCredentialBodiesError
sealed interface GetAllCredentialsError
sealed interface GetCredentialByIdError
sealed interface GetCredentialBodiesByCredentialIdError
sealed interface DeleteCredentialError
sealed interface GetCredentialClaimsError
sealed interface GetCredentialClaimDisplayError
sealed interface GetCredentialClaimDataError
sealed interface MapToCredentialClaimDataError

internal fun CredentialRawRepositoryError.toGetAllCredentialBodiesError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun ParseSdJwtError.toGetAllCredentialBodiesError() = when (this) {
    ParseSdJwtError.InvalidDigestArray,
    ParseSdJwtError.InvalidDisclosure,
    ParseSdJwtError.InvalidJwt -> SsiError.Unexpected(null)

    is ParseSdJwtError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialRepositoryError.toGetAllCredentialsError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialRepositoryError.toGetCredentialByIdError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialRawRepositoryError.toGetCredentialBodiesByCredentialIdError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun ParseSdJwtError.toGetCredentialBodiesByCredentialIdError() = when (this) {
    ParseSdJwtError.InvalidDigestArray,
    ParseSdJwtError.InvalidDisclosure,
    ParseSdJwtError.InvalidJwt -> SsiError.Unexpected(null)

    is ParseSdJwtError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialRepositoryError.toDeleteCredentialError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialClaimRepositoryError.toGetCredentialClaimsError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun CredentialClaimDisplayRepositoryError.toGetCredentialClaimDisplayError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun GetCredentialClaimsError.toGetCredentialClaimDataError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun GetCredentialClaimDisplayError.toGetCredentialClaimDataError() = when (this) {
    is SsiError.Unexpected -> SsiError.Unexpected(cause)
}

internal fun MapToCredentialClaimDataError.toGetCredentialClaimDataError() = when (this) {
    is SsiError.Unexpected -> this
}

internal fun Throwable.toMapToCredentialClaimDataError(): MapToCredentialClaimDataError {
    Timber.e(this)
    return SsiError.Unexpected(this)
}
