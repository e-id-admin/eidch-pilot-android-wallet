package ch.admin.foitt.pilotwallet.platform.ssi.data.repository

import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialDisplayRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialDisplayRepo
import ch.admin.foitt.pilotwallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CredentialDisplayRepoImpl @Inject constructor(
    databaseRepository: DatabaseRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CredentialDisplayRepo {
    override suspend fun insertAll(
        credentialDisplays: Collection<CredentialDisplay>
    ): Result<Unit, CredentialDisplayRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().insertAll(credentialDisplays)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllGroupedCredentialDisplaysFlow(): Flow<Map<Long, Collection<CredentialDisplay>>> = daoFlow
        .flatMapLatest {
                crtDao ->
            crtDao?.getAllGroupedByCredentialId() ?: flowOf(mapOf())
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCredentialDisplaysFlow(credentialId: Long): Flow<Collection<CredentialDisplay>> = daoFlow
        .flatMapLatest {
                crtDao ->
            crtDao?.getAsFlow(credentialId) ?: flowOf(listOf())
        }

    private suspend fun dao(): CredentialDisplayDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = databaseRepository.credentialDisplayDaoFlow
}
