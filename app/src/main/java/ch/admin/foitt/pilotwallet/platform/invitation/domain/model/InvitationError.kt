package ch.admin.foitt.pilotwallet.platform.invitation.domain.model

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.FetchPresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.CredentialOfferError
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.FetchCredentialError

interface InvitationError {
    data class UnknownSchema(val message: String) : ValidateInvitationError
    data class InvalidUri(val message: String) : GetPresentationRequestError, ValidateInvitationError
    data object InvalidCredentialInvitation : ProcessInvitationError
    data object NoCredentialsFound : GetCredentialOfferError, ValidateInvitationError
    data class UnsupportedGrantType(val message: String) : GetCredentialOfferError, ValidateInvitationError
    data class DeserializationFailed(val message: String) : GetCredentialOfferError, ValidateInvitationError
    data object NetworkError : ProcessInvitationError, GetPresentationRequestError, ValidateInvitationError
    data object UnknownVerifier : ProcessInvitationError, GetPresentationRequestError, ValidateInvitationError
    data object UnknownIssuer : ProcessInvitationError
    data object EmptyWallet : ProcessInvitationError
    data object Unexpected : ProcessInvitationError
    data object NoMatchingCredential : ProcessInvitationError
    data object InvalidInput : ProcessInvitationError
    data object InvalidPresentationRequest : GetPresentationRequestError, ValidateInvitationError
}

sealed interface ProcessInvitationError : InvitationError
sealed interface GetCredentialOfferError : InvitationError
sealed interface GetPresentationRequestError : InvitationError
sealed interface ValidateInvitationError : InvitationError

//region Error to Error mappings
internal fun FetchPresentationRequestError.toGetPresentationRequestError(): GetPresentationRequestError = when (this) {
    PresentationRequestError.CertificateNotPinnedError -> InvitationError.UnknownVerifier
    PresentationRequestError.NetworkError -> InvitationError.NetworkError
    is PresentationRequestError.Unexpected -> InvitationError.InvalidPresentationRequest
}

internal fun GetPresentationRequestError.toValidateInvitationError(): ValidateInvitationError = when (this) {
    is InvitationError.InvalidUri -> this
    is InvitationError.NetworkError -> this
    is InvitationError.UnknownVerifier -> this
    is InvitationError.InvalidPresentationRequest -> this
}

internal fun GetCredentialOfferError.toValidateInvitationError(): ValidateInvitationError = when (this) {
    is InvitationError.DeserializationFailed -> this
    is InvitationError.NoCredentialsFound -> this
    is InvitationError.UnsupportedGrantType -> this
}

internal fun FetchCredentialError.toProcessInvitationError(): ProcessInvitationError = when (this) {
    CredentialOfferError.CertificateNotPinned -> InvitationError.UnknownIssuer
    CredentialOfferError.IntegrityCheckFailed,
    CredentialOfferError.InvalidGrant,
    CredentialOfferError.UnsupportedGrantType,
    is CredentialOfferError.MissingMandatoryField,
    CredentialOfferError.InvalidCredentialOffer,
    CredentialOfferError.UnsupportedCredentialFormat,
    CredentialOfferError.UnsupportedCredentialType,
    CredentialOfferError.UnsupportedProofType,
    CredentialOfferError.UnsupportedCryptographicSuite,
    CredentialOfferError.CredentialParsingError -> InvitationError.InvalidCredentialInvitation
    CredentialOfferError.NetworkError -> InvitationError.NetworkError
    CredentialOfferError.DatabaseError,
    is CredentialOfferError.Unexpected -> InvitationError.Unexpected
}

internal fun ValidateInvitationError.toProcessInvitationError(): ProcessInvitationError = when (this) {
    is InvitationError.InvalidUri,
    is InvitationError.UnknownSchema,
    is InvitationError.InvalidPresentationRequest -> InvitationError.InvalidInput
    is InvitationError.NetworkError -> this
    is InvitationError.UnknownVerifier -> this
    is InvitationError.UnsupportedGrantType,
    is InvitationError.DeserializationFailed,
    is InvitationError.NoCredentialsFound -> InvitationError.InvalidCredentialInvitation
}
//endregion
