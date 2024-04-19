package ch.admin.foitt.pilotwallet.platform.ssi.domain.usecase

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw

fun interface GetCredentialRaw {
    suspend operator fun invoke(credentialId: Long): List<CredentialRaw>
}
