package ch.admin.foitt.pilotwallet.platform.database.domain.repository

import ch.admin.foitt.pilotwallet.platform.database.domain.AppDatabase
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseError
import com.github.michaelbull.result.Result

interface SetupDatabaseRepository {

    fun create(password: ByteArray): Result<AppDatabase, DatabaseError.SetupFailed>
}
