package ch.admin.foitt.pilotwallet.platform.crypto.domain.usecase

import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashDataError
import ch.admin.foitt.pilotwallet.platform.crypto.domain.model.HashedData
import com.github.michaelbull.result.Result

interface HashDataWithSalt {
    suspend operator fun invoke(data: String, salt: ByteArray?): Result<HashedData, HashDataError>
}
