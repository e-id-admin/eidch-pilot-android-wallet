package ch.admin.foitt.pilotwallet.platform.ssi.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRawRepositoryError
import com.github.michaelbull.result.Result

interface CredentialRawRepo {
    suspend fun insert(credentialRaw: CredentialRaw): Result<Long, CredentialRawRepositoryError>
    suspend fun getByCredentialId(credentialId: Long): Result<List<CredentialRaw>, CredentialRawRepositoryError>
    suspend fun getAll(): Result<List<CredentialRaw>, CredentialRawRepositoryError>
}
