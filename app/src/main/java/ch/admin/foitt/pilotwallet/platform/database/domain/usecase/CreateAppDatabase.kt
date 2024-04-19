package ch.admin.foitt.pilotwallet.platform.database.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.database.domain.model.CreateDatabaseError
import com.github.michaelbull.result.Result

interface CreateAppDatabase {

    @CheckResult
    suspend operator fun invoke(
        passphrase: ByteArray
    ): Result<Unit, CreateDatabaseError>
}
