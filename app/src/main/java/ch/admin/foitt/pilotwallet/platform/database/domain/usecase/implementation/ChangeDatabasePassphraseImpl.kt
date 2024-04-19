package ch.admin.foitt.pilotwallet.platform.database.domain.usecase.implementation

import ch.admin.foitt.pilotwallet.platform.database.domain.model.ChangeDatabasePassphraseError
import ch.admin.foitt.pilotwallet.platform.database.domain.model.DatabaseWrapper
import ch.admin.foitt.pilotwallet.platform.database.domain.usecase.ChangeDatabasePassphrase
import ch.admin.foitt.pilotwallet.platform.di.IoDispatcher
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChangeDatabasePassphraseImpl @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val databaseWrapper: DatabaseWrapper,
) : ChangeDatabasePassphrase {
    override suspend fun invoke(newPassphrase: ByteArray): Result<Unit, ChangeDatabasePassphraseError> = withContext(coroutineDispatcher) {
        databaseWrapper.changePassphrase(newPassphrase)
    }
}
