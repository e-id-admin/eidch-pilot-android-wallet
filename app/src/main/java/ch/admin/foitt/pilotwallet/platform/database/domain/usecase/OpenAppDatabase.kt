package ch.admin.foitt.pilotwallet.platform.database.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.database.domain.model.OpenDatabaseError
import com.github.michaelbull.result.Result

interface OpenAppDatabase {

    @CheckResult
    suspend operator fun invoke(
        passphrase: ByteArray
    ): Result<Unit, OpenDatabaseError>
}
