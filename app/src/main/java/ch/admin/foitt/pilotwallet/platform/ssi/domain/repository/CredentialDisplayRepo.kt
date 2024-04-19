package ch.admin.foitt.pilotwallet.platform.ssi.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialDisplayRepositoryError
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.Flow

interface CredentialDisplayRepo {
    suspend fun insertAll(credentialDisplays: Collection<CredentialDisplay>): Result<Unit, CredentialDisplayRepositoryError>
    fun getAllGroupedCredentialDisplaysFlow(): Flow<Map<Long, Collection<CredentialDisplay>>>
    fun getCredentialDisplaysFlow(credentialId: Long): Flow<Collection<CredentialDisplay>>
}
