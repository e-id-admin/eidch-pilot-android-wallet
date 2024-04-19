package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import android.content.Context
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.CheckDatabasePassphrase
import com.github.michaelbull.result.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckDatabasePassphraseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseWrapper: DatabaseWrapper,
) : CheckDatabasePassphrase {
    override suspend fun invoke(passphrase: ByteArray): Result<Unit, OpenDatabaseError> {
        return databaseWrapper.checkIfCorrectPassphrase(context, passphrase)
    }
}
