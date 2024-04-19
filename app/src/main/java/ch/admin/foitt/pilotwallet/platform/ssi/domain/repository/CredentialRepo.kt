package ch.admin.foitt.pilotwallet.platform.ssi.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRepositoryError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface CredentialRepo {
    suspend fun insert(credential: Credential): Result<Long, CredentialRepositoryError>
    suspend fun getAll(): Result<List<Credential>, CredentialRepositoryError>
    suspend fun getById(id: Long): Result<Credential?, CredentialRepositoryError>
    fun getByIdFlow(credentialId: Long): Flow<Credential?>
    fun getCredentialStatusAsFlow(id: Long): Flow<CredentialStatus>
    suspend fun update(credential: Credential): Result<Int, CredentialRepositoryError>
    suspend fun deleteById(id: Long): Result<Unit, CredentialRepositoryError>
    fun getAllFlow(): Flow<List<Credential>>
}
