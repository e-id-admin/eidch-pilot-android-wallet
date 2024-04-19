package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import android.content.Context
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CreateDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CreateAppDatabase
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CreateAppDatabaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseWrapper: DatabaseWrapper,
) : CreateAppDatabase {

    override suspend fun invoke(passphrase: ByteArray): Result<Unit, CreateDatabaseError> {
        return databaseWrapper.createDatabase(context = context, passphrase = passphrase).map {}
    }
}
