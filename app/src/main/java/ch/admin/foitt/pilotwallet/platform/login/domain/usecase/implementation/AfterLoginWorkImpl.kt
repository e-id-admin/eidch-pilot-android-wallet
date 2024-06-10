package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseState
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.AfterLoginWork
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import timber.log.Timber
import javax.inject.Inject

class AfterLoginWorkImpl @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val updateAllCredentialStatuses: UpdateAllCredentialStatuses,
) : AfterLoginWork {
    override suspend fun invoke() {
        databaseRepository.databaseState.collect { dbState ->
            when (dbState) {
                DatabaseState.OPEN -> {
                    Timber.d("After login work: Start updating the credential statuses...")
                    updateAllCredentialStatuses()
                }
                DatabaseState.CLOSED -> Timber.d("After login work: DB closed")
            }
        }
    }
}
