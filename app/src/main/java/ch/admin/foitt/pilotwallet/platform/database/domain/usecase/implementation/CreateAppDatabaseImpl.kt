package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.CreateDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.repository.DatabaseRepository
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CreateAppDatabase
import com.github.michaelbull.result.Result
import javax.inject.Inject

class CreateAppDatabaseImpl @Inject constructor(
    private val databaseRepository: DatabaseRepository,
) : CreateAppDatabase {

    override suspend fun invoke(passphrase: ByteArray): Result<Unit, CreateDatabaseError> = databaseRepository.createDatabase(passphrase)
}
