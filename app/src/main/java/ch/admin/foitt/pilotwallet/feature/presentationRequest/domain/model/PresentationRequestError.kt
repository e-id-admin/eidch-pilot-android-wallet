package ch.admin.foitt.pilotwallet.feature.presentationRequest.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreatePresentationRequestBodyError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.CreateVerifiablePresentationTokenError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.SendPresentationError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.CreateDisclosedSdJwtError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseRawSdJwtError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRawRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimDataError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.GetCredentialClaimsError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError as OpenIdPresentationRequestError

interface PresentationRequestError {
    object CredentialNoFound : SubmitPresentationError
    object RawSdJwtParsingError : GeneratePresentationMetadataError, SubmitPresentationError
    object ValidationError : SubmitPresentationError
    object InvalidUrl : SubmitPresentationError
    data class Unexpected(
        val throwable: Throwable?
    ) : GeneratePresentationMetadataError, GetCredentialRawsError, SubmitPresentationError
}

sealed interface GeneratePresentationMetadataError
sealed interface GetCredentialRawsError
sealed interface SubmitPresentationError

fun CredentialRawRepositoryError.toGetCredentialRawsError() = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun ParseRawSdJwtError.toGeneratePresentationMetadataError() = when (this) {
    is ParseRawSdJwtError.InvalidJwt,
    ParseRawSdJwtError.InvalidDisclosure -> PresentationRequestError.RawSdJwtParsingError
    is ParseRawSdJwtError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun CredentialRawRepositoryError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun CreateDisclosedSdJwtError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    CreateDisclosedSdJwtError.InvalidSdJwt -> PresentationRequestError.RawSdJwtParsingError
    is CreateDisclosedSdJwtError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun CreatePresentationRequestBodyError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    is OpenIdPresentationRequestError.Unexpected -> PresentationRequestError.Unexpected(throwable)
}

fun CreateVerifiablePresentationTokenError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    is OpenIdPresentationRequestError.Unexpected -> PresentationRequestError.Unexpected(throwable)
}

fun SendPresentationError.toSubmitPresentationError(): SubmitPresentationError = when (this) {
    OpenIdPresentationRequestError.CertificateNotPinnedError,
    OpenIdPresentationRequestError.NetworkError -> PresentationRequestError.Unexpected(null)
    is OpenIdPresentationRequestError.Unexpected -> PresentationRequestError.Unexpected(throwable)
    OpenIdPresentationRequestError.ValidationError -> PresentationRequestError.ValidationError
}

fun GetCredentialClaimDataError.toGeneratePresentationMetadataError(): GeneratePresentationMetadataError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}

fun GetCredentialClaimsError.toGeneratePresentationMetadataError(): GeneratePresentationMetadataError = when (this) {
    is SsiError.Unexpected -> PresentationRequestError.Unexpected(cause)
}
