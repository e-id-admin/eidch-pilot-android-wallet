package ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.model

sealed interface CredentialValidityStatus {
    data object Valid : CredentialValidityStatus
    data object Suspended : CredentialValidityStatus
    data object Revoked : CredentialValidityStatus
    data object Expired : CredentialValidityStatus
    data object Unknown : CredentialValidityStatus
}
