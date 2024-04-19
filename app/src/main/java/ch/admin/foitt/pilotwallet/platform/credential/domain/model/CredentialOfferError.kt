package ch.admin.foitt.pilotwallet.platform.credential.domain.model

import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerCredentialInformationError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchIssuerPublicKeyInfoError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.FetchVerifiableCredentialError
import ch.admin.foitt.pilotwallet.platform.sdjwt.domain.model.ParseSdJwtError
import ch.admin.foitt.openid4vc.domain.model.credentialoffer.CredentialOfferError as OpenIdCredentialOfferError

sealed interface CredentialOfferError {
    object InvalidGrant : FetchCredentialError
    object UnsupportedGrantType : FetchCredentialError
    object UnsupportedCredentialType : FetchCredentialOfferInformationError, FetchCredentialError
    object UnsupportedProofType : FetchCredentialError
    object UnsupportedCryptographicSuite : FetchCredentialError
    object InvalidCredentialOffer : FetchCredentialError
    object UnsupportedCredentialFormat : SaveCredentialError, FetchCredentialError
    object CredentialParsingError : SaveCredentialError, FetchCredentialError
    data class MissingMandatoryField(val missingField: String) : SaveCredentialError, FetchCredentialError
    object IntegrityCheckFailed : FetchCredentialError
    object InvalidSdJwt : VerifySdJwtError
    object DatabaseError : SaveCredentialError, GetCredentialRawError, FetchCredentialError
    object NetworkError : FetchCredentialOfferInformationError, FetchCredentialError, VerifyJwtError
    object CertificateNotPinned : FetchCredentialOfferInformationError, FetchCredentialError, VerifyJwtError
    data class Unexpected(val cause: Throwable?) :
        FetchCredentialOfferInformationError,
        FetchCredentialError,
        IssuerCredentialInformationInMemoryRepoError,
        SaveCredentialError,
        GetCredentialOfferDataError,
        CheckCredentialIntegrityError,
        VerifyJwtError,
        VerifyJwtTimestampsError,
        VerifySdJwtError
}

sealed interface FetchCredentialOfferInformationError
sealed interface FetchCredentialError
sealed interface SaveCredentialError
sealed interface GetCredentialRawError
sealed interface IssuerCredentialInformationInMemoryRepoError
sealed interface GetCredentialOfferDataError
sealed interface CheckCredentialIntegrityError

sealed interface VerifyJwtError
sealed interface VerifyJwtTimestampsError
sealed interface VerifySdJwtError

fun FetchIssuerCredentialInformationError.toFetchCredentialError(): FetchCredentialError = when (this) {
    OpenIdCredentialOfferError.CertificateNotPinnedInfoError -> CredentialOfferError.CertificateNotPinned
    OpenIdCredentialOfferError.NetworkInfoError -> CredentialOfferError.NetworkError
    OpenIdCredentialOfferError.UnsupportedCredentialType -> CredentialOfferError.UnsupportedCredentialType
    is OpenIdCredentialOfferError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

fun FetchVerifiableCredentialError.toFetchCredentialError(): FetchCredentialError = when (this) {
    OpenIdCredentialOfferError.InvalidGrant -> CredentialOfferError.InvalidGrant
    OpenIdCredentialOfferError.InvalidCredentialOffer -> CredentialOfferError.InvalidCredentialOffer
    OpenIdCredentialOfferError.UnsupportedCredentialType -> CredentialOfferError.UnsupportedCredentialType
    OpenIdCredentialOfferError.UnsupportedCryptographicSuite -> CredentialOfferError.UnsupportedCryptographicSuite
    OpenIdCredentialOfferError.UnsupportedGrantType -> CredentialOfferError.UnsupportedGrantType
    OpenIdCredentialOfferError.UnsupportedProofType -> CredentialOfferError.UnsupportedProofType
    OpenIdCredentialOfferError.NetworkInfoError -> CredentialOfferError.NetworkError
    OpenIdCredentialOfferError.CertificateNotPinnedInfoError -> CredentialOfferError.CertificateNotPinned
    is OpenIdCredentialOfferError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

fun ParseSdJwtError.toSaveCredentialError(): SaveCredentialError = when (this) {
    ParseSdJwtError.InvalidDigestArray -> CredentialOfferError.CredentialParsingError
    ParseSdJwtError.InvalidDisclosure -> CredentialOfferError.CredentialParsingError
    ParseSdJwtError.InvalidJwt -> CredentialOfferError.CredentialParsingError
    is ParseSdJwtError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

fun ParseSdJwtError.toVerifySdJwtError(): VerifySdJwtError = when (this) {
    ParseSdJwtError.InvalidDigestArray -> CredentialOfferError.InvalidSdJwt
    ParseSdJwtError.InvalidDisclosure -> CredentialOfferError.InvalidSdJwt
    ParseSdJwtError.InvalidJwt -> CredentialOfferError.InvalidSdJwt
    is ParseSdJwtError.Unexpected -> CredentialOfferError.Unexpected(cause)
}

fun FetchIssuerPublicKeyInfoError.toVerifyJwtError(): VerifyJwtError = when (this) {
    OpenIdCredentialOfferError.CertificateNotPinnedInfoError -> CredentialOfferError.CertificateNotPinned
    OpenIdCredentialOfferError.NetworkInfoError -> CredentialOfferError.NetworkError
    is OpenIdCredentialOfferError.Unexpected -> CredentialOfferError.Unexpected(cause)
}
