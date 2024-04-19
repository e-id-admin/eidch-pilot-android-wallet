package ch.admin.foitt.pilotwallet.platform.login.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseState
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseStateRepository
import ch.admin.foitt.pilotwallet.platform.login.domain.usecase.AfterLoginWork
import ch.admin.foitt.pilotwallet.platform.updateCredentialStatus.domain.usecase.UpdateAllCredentialStatuses
import timber.log.Timber
import javax.inject.Inject

class AfterLoginWorkImpl @Inject constructor(
    private val databaseStateRepository: DatabaseStateRepository,
    private val updateAllCredentialStatuses: UpdateAllCredentialStatuses,
) : AfterLoginWork {
    override suspend fun invoke() {
        databaseStateRepository.state.collect { dbState ->
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
