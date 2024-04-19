package ch.admin.foitt.pilotwallet.platform.ssi.data.repository

import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialRawDao
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialRaw
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialRawRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialRawRepo
import ch.admin.foitt.pilotwallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CredentialRawRepoImpl @Inject constructor(
    databaseWrapper: DatabaseWrapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CredentialRawRepo {
    override suspend fun insert(credentialRaw: CredentialRaw) = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().insert(credentialRaw)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getByCredentialId(credentialId: Long): Result<List<CredentialRaw>, CredentialRawRepositoryError> =
        runSuspendCatching {
            withContext(ioDispatcher) {
                dao().getByCredentialId(credentialId)
            }
        }.mapError { throwable ->
            Timber.e(throwable)
            SsiError.Unexpected(throwable)
        }

    override suspend fun getAll(): Result<List<CredentialRaw>, CredentialRawRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().getAll()
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    private suspend fun dao(): CredentialRawDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = databaseWrapper.credentialRawDao
}
