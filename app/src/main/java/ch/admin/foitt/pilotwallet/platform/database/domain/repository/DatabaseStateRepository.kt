package ch.admin.foitt.pilotwallet.platform.database.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseState
import kotlinx.coroutines.flow.StateFlow

interface DatabaseStateRepository {
    val state: StateFlow<DatabaseState>
}
