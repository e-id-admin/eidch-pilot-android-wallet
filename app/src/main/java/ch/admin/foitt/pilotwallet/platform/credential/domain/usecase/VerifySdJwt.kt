package ch.admin.foitt.pilotwallet.platform.credential.domain.usecase

import androidx.annotation.CheckResult
import ch.admin.foitt.pilotwallet.platform.credential.domain.model.VerifySdJwtError
import com.github.michaelbull.result.Result

interface VerifySdJwt {
    @CheckResult
    suspend operator fun invoke(sdjwt: String): Result<Boolean, VerifySdJwtError>
}
