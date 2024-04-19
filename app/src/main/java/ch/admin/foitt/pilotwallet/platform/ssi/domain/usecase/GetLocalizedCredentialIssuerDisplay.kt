package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay

interface GetLocalizedCredentialIssuerDisplay {
    suspend operator fun invoke(credentialId: Long): CredentialIssuerDisplay?
}
