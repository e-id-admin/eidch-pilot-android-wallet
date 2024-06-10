package ch.admin.foitt.pilotwallet.platform.ssi.data.repository

import ch.admin.foitt.pilotwallet.platform.database.data.dao.CredentialIssuerDisplayDao
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CredentialIssuerDisplay
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.CredentialIssuerDisplayRepositoryError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.model.SsiError
import ch.admin.foitt.pilotwallet.platform.ssi.domain.repository.CredentialIssuerDisplayRepo
import ch.admin.foitt.pilotwallet.platform.utils.suspendUntilNonNull
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CredentialIssuerDisplayRepoImpl @Inject constructor(
    databaseRepository: DatabaseRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CredentialIssuerDisplayRepo {
    override suspend fun insertAll(
        credentialIssuerDisplays: Collection<CredentialIssuerDisplay>
    ): Result<Unit, CredentialIssuerDisplayRepositoryError> = runSuspendCatching {
        withContext(ioDispatcher) {
            dao().insertAll(credentialIssuerDisplays)
        }
    }.mapError { throwable ->
        Timber.e(throwable)
        SsiError.Unexpected(throwable)
    }

    override suspend fun getByCredentialId(credentialId: Long): List<CredentialIssuerDisplay> =
        runSuspendCatching {
            withContext(ioDispatcher) {
                dao().getByCredentialId(credentialId)
            }
        }.mapError { throwable ->
            Timber.e(throwable)
        }.getOr(emptyList())

    private suspend fun dao(): CredentialIssuerDisplayDao = suspendUntilNonNull { daoFlow.value }
    private val daoFlow = databaseRepository.credentialIssuerDisplayDao
}
