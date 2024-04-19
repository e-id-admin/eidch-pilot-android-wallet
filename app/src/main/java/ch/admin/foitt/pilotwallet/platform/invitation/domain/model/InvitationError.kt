package ch.admin.foitt.pilotwallet.platform.invitation.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.FetchPresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError

interface InvitationError {
    data class UnknownSchema(val message: String) : ValidateInvitationError
    data class InvalidUri(val message: String) : GetPresentationRequestError, ValidateInvitationError
    data object InvalidInvitation : ProcessInvitationError, ValidateInvitationError, GetPresentationRequestError
    data object NoCredentialsFound : GetCredentialOfferError, ValidateInvitationError
    data class UnsupportedGrantType(val message: String) : GetCredentialOfferError, ValidateInvitationError
    data class DeserializationFailed(val message: String) : GetCredentialOfferError, ValidateInvitationError
    data object NetworkError : GetPresentationRequestError, ValidateInvitationError
    data object UnknownVerifier : GetPresentationRequestError, ValidateInvitationError
}

sealed interface ProcessInvitationError : InvitationError
sealed interface GetCredentialOfferError : InvitationError
sealed interface GetPresentationRequestError : InvitationError
sealed interface ValidateInvitationError : InvitationError

//region Error to Error mappings
internal fun FetchPresentationRequestError.toGetPresentationRequestError(): GetPresentationRequestError = when (this) {
    PresentationRequestError.CertificateNotPinnedError -> InvitationError.UnknownVerifier
    PresentationRequestError.NetworkError -> InvitationError.NetworkError
    is PresentationRequestError.Unexpected -> InvitationError.InvalidInvitation
}

internal fun GetPresentationRequestError.toValidateInvitationError(): ValidateInvitationError = when (this) {
    is InvitationError.InvalidUri -> this
    is InvitationError.NetworkError -> this
    is InvitationError.UnknownVerifier -> this
    is InvitationError.InvalidInvitation -> this
}

internal fun GetCredentialOfferError.toValidateInvitationError(): ValidateInvitationError = when (this) {
    is InvitationError.DeserializationFailed -> this
    is InvitationError.NoCredentialsFound -> this
    is InvitationError.UnsupportedGrantType -> this
}
//endregion
