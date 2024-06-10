package ch.admin.foitt.pilotwallet.platform.ssi.data.repository

import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialDao
import ch.admin.foitt.pilotwallet.platform.database.domain.model.Credential
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialStatus
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRepo
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
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class CredentialRepoImpl @Inject constructor(
    databaseRepository: DatabaseRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CredentialRepo {
    override suspend fun insert(credential: Credential): Result<Long, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().insert(credential)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getAll(): Result<List<Credential>, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().getAll()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getById(id: Long): Result<Credential?, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().getById(id)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override fun getCredentialStatusAsFlow(id: Long): Flow<CredentialStatus> = daoFlow
        .flatMapLatest { currentDao ->
            currentDao?.getCredentialStatusByIdAsFlow(id) ?: flowOf(CredentialStatus.UNKNOWN)
        }

    override suspend fun deleteById(id: Long): Result<Unit, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().deleteById(id)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun update(credential: Credential): Result<Int, CredentialRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().update(credential.copy(updatedAt = Instant.now().epochSecond))
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override fun getAllFlow(): Flow<List<Credential>> = daoFlow.flatMapLatest { currentDao ->
        currentDao?.getAllFlow() ?: flowOf(listOf())
    }

    override fun getByIdFlow(credentialId: Long): Flow<Credential?> = daoFlow.flatMapLatest { currentDao ->
        currentDao?.getByIdFlow(credentialId) ?: flowOf(null)
    }

    private suspend fun dao(): CredentialDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = databaseRepository.credentialDaoFlow
}
