package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model

import ch.admin.foitt.openid4vc.domain.model.vcStatus.FetchVCStatusError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialBodiesByCredentialIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialByIdError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError

interface UpdateCredentialStatusError {
    data class Unexpected(val cause: Throwable?) :
        UpdateAllCredentialStatusesError,
        UpdateOneCredentialStatusError,
        CheckCredentialValidityError
}

sealed interface UpdateAllCredentialStatusesError
sealed interface UpdateOneCredentialStatusError
sealed interface CheckCredentialValidityError

internal fun GetCredentialBodiesByCredentialIdError.toCheckCredentialValidityError(): CheckCredentialValidityError = when (this) {
    is SsiError.Unexpected -> UpdateCredentialStatusError.Unexpected(cause)
}

internal fun CredentialRepositoryError.toCheckCredentialValidityError(): CheckCredentialValidityError = when (this) {
    is SsiError.Unexpected -> UpdateCredentialStatusError.Unexpected(cause)
}

internal fun FetchVCStatusError.toCheckCredentialValidityError(): CheckCredentialValidityError = when (this) {
    FetchVCStatusError.CertificateNotPinnedError,
    FetchVCStatusError.NetworkError,
    FetchVCStatusError.UnsupportedStatusListFormat -> UpdateCredentialStatusError.Unexpected(null)
    is FetchVCStatusError.Unexpected -> UpdateCredentialStatusError.Unexpected(throwable)
}

internal fun GetCredentialByIdError.toUpdateOneCredentialError(): UpdateOneCredentialStatusError = when (this) {
    is SsiError.Unexpected -> UpdateCredentialStatusError.Unexpected(cause)
}
