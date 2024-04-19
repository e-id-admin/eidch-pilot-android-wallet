package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import javax.inject.Inject

class CloseAppDatabaseImpl @Inject constructor(
    private val databaseWrapper: DatabaseWrapper,
) : CloseAppDatabase {
    override suspend fun invoke() {
        databaseWrapper.close()
    }
}
