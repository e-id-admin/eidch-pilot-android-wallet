package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CloseAppDatabase
import javax.inject.Inject

class CloseAppDatabaseImpl @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : CloseAppDatabase {
    override suspend fun invoke() {
        databaseRepository.close()
    }
}
