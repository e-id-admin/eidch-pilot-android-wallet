package ch.admin.foitt.pilotwallet.platform.database.data.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseState
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseStateRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class DatabaseStateRepositoryImpl @Inject constructor(
    databaseWrapper: DatabaseWrapper,
) : DatabaseStateRepository {
    override val state: StateFlow<DatabaseState> = databaseWrapper.databaseStatus
}
