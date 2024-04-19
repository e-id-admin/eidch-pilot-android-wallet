package ch.admin.foitt.pilotwallet.platform.ssi.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialIssuerDisplayRepositoryError
import com.github.michaelbull.result.Result

interface CredentialIssuerDisplayRepo {
    suspend fun insertAll(
        credentialIssuerDisplays: Collection<CredentialIssuerDisplay>
    ): Result<Unit, CredentialIssuerDisplayRepositoryError>

    suspend fun getByCredentialId(credentialId: Long): List<CredentialIssuerDisplay>
}
