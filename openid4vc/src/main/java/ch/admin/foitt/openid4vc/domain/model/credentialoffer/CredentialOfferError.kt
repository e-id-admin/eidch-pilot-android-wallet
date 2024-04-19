package ch.admin.foitt.openid4vc.domain.model.credentialoffer

import ch.admin.foitt.openid4vc.domain.model.CreateES512KeyPairError
import ch.admin.foitt.openid4vc.domain.model.KeyPairError

interface CredentialOfferError {
    data object InvalidGrant : FetchVerifiableCredentialError
    data object UnsupportedGrantType : FetchVerifiableCredentialError
    data object UnsupportedCredentialType : FetchVerifiableCredentialError, FetchIssuerCredentialInformationError
    data object UnsupportedProofType : FetchVerifiableCredentialError
    data object UnsupportedCryptographicSuite : FetchVerifiableCredentialError
    data object InvalidCredentialOffer : FetchVerifiableCredentialError
    data object NetworkInfoError :
        FetchIssuerCredentialInformationError,
        FetchVerifiableCredentialError,
        FetchIssuerConfigurationError,
        FetchIssuerPublicKeyInfoError
    data object CertificateNotPinnedInfoError :
        FetchIssuerCredentialInformationError,
        FetchVerifiableCredentialError,
        FetchIssuerConfigurationError,
        FetchIssuerPublicKeyInfoError
    data class Unexpected(val cause: Throwable?) :
        FetchIssuerCredentialInformationError,
        FetchVerifiableCredentialError,
        FetchIssuerConfigurationError,
        FetchIssuerPublicKeyInfoError
}

sealed interface FetchIssuerCredentialInformationError
sealed interface FetchVerifiableCredentialError
sealed interface FetchIssuerConfigurationError
sealed interface FetchIssuerPublicKeyInfoError

fun CreateES512KeyPairError.toCredentialOfferError() = when (this) {
    is KeyPairError.Unexpected -> CredentialOfferError.Unexpected(throwable)
}
